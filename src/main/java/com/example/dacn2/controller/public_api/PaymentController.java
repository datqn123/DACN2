package com.example.dacn2.controller.public_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.request.BookingRequest;
import com.example.dacn2.dto.response.PaymentLinkResponse;
import com.example.dacn2.entity.booking.Booking;
import com.example.dacn2.service.page.BookingService;

import jakarta.servlet.http.HttpServletResponse;

import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;

import com.example.dacn2.dto.response.BookingResponse;
import com.example.dacn2.entity.booking.BookingStatus;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PayOS payOS;

    @Autowired
    private BookingService bookingService;

    @Value("${app.frontend.url:https://tripgo-qmdo.onrender.com}")
    private String frontendBaseUrl;

    // URL của backend để PayOS callback về
    @Value("${app.backend.url:https://tripgo-qmdo.onrender.com}")
    private String backendBaseUrl;

    @Value("${payos.client-id}")
    private String debugClientId;

    @Value("${payos.api-key}")
    private String debugApiKey;

    @Value("${payos.checksum-key}")
    private String debugChecksumKey;

    private static final double price = 2000;

    /**
     * Tạo link thanh toán PayOS và trả về QR code + thông tin chuyển khoản
     */
    @PostMapping("/create-payment-link")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<PaymentLinkResponse> createPaymentLink(@RequestBody BookingRequest request) {
        // 1. Tạo Booking trong DB trước
        Booking booking = bookingService.createBooking(request);
        long bookingId = booking.getId(); // Dùng ID làm orderCode cho PayOS

        // 2. Lấy số tiền từ booking (finalPrice đã tính discount)
        // double price = booking.getFinalPrice() != null ? booking.getFinalPrice() :
        // booking.getTotalPrice();
        long amount = (long) price;

        if (amount <= 0) {
            throw new RuntimeException("Số tiền thanh toán không hợp lệ: " + amount);
        }

        try {
            // 3. Tạo request gửi sang PayOS
            CreatePaymentLinkRequest paymentRequest = CreatePaymentLinkRequest.builder()
                    .orderCode(bookingId)
                    .amount(amount)
                    .description("DH " + bookingId)
                    .returnUrl(backendBaseUrl + "/api/payment/success")
                    .cancelUrl(backendBaseUrl + "/api/payment/cancel")
                    .build();

            // 4. Gọi PayOS tạo link thanh toán
            CreatePaymentLinkResponse response = payOS.paymentRequests().create(paymentRequest);

            // 5. Tạo response với đầy đủ thông tin để hiển thị trên UI
            PaymentLinkResponse paymentInfo = PaymentLinkResponse.builder()
                    .orderCode(bookingId)
                    .amount(amount)
                    .description("DH " + bookingId)
                    .checkoutUrl(response.getCheckoutUrl())
                    .qrCode(response.getQrCode())
                    .accountNumber(response.getAccountNumber())
                    .accountName(response.getAccountName())
                    .build();

            return ApiResponse.<PaymentLinkResponse>builder()
                    .result(paymentInfo)
                    .message("Tạo link thanh toán thành công")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi tạo link thanh toán: " + e.getMessage());
        }
    }

    /**
     * Callback khi thanh toán THÀNH CÔNG - PayOS redirect về đây, sau đó redirect
     * về Frontend
     */
    @GetMapping("/success")
    public void paymentSuccess(
            @RequestParam String code,
            @RequestParam Long orderCode,
            @RequestParam String status,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) Boolean cancel,
            HttpServletResponse response) throws Exception {

        try {
            // Xác thực lại với PayOS để đảm bảo an toàn
            var paymentLinkData = payOS.paymentRequests().get(orderCode);

            if ("PAID".equals(String.valueOf(paymentLinkData.getStatus()))) {
                bookingService.confirmPayment(orderCode);
                System.out.println("✅ Thanh toán thành công (Verified)! Mã đơn: " + orderCode);
            } else {
                System.out
                        .println("⚠️ Thanh toán chưa hoàn tất hoặc lỗi. Status PayOS: " + paymentLinkData.getStatus());
                status = "FAILED";
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi verify payment: " + e.getMessage());
            status = "ERROR";
        }

        // Redirect về frontend với thông tin thanh toán
        String frontendUrl = frontendBaseUrl + "/payment/result"
                + "?status=" + status
                + "&orderCode=" + orderCode
                + "&message=success";

        response.sendRedirect(frontendUrl);
    }

    /**
     * Callback khi HỦY thanh toán - PayOS redirect về đây, sau đó redirect về
     * Frontend
     */
    @GetMapping("/cancel")
    public void paymentCancel(
            @RequestParam(required = false) Long orderCode,
            @RequestParam(required = false) String status,
            HttpServletResponse response) throws Exception {

        System.out.println("❌ Thanh toán bị hủy! Mã đơn: " + orderCode);

        // Redirect về frontend với thông tin hủy
        String frontendUrl = frontendBaseUrl + "/payment/result"
                + "?status=CANCELLED"
                + "&orderCode=" + orderCode
                + "&message=cancelled";

        response.sendRedirect(frontendUrl);
    }

    /**
     * Webhook nhận callback từ PayOS khi thanh toán thành công (Server-to-Server)
     */
    @PostMapping({ "", "/payos-webhook" })
    public ResponseEntity<String> handlePayOSWebhook(@RequestBody Webhook webhookBody) {
        System.out.println("🔔 WEBHOOK RECEIVED from PayOS");
        System.out.println("Data: " + webhookBody.getData());
        System.out.println("Signature: " + webhookBody.getSignature());

        try {
            // Xác thực webhook
            WebhookData data = payOS.webhooks().verify(webhookBody);
            long bookingId = data.getOrderCode();

            System.out.println("✅ Signature Verified! Processing Booking ID: " + bookingId);

            // Cập nhật booking status = CONFIRMED, isPaid = true
            bookingService.confirmPayment(bookingId);

            System.out.println("✅ Payment Confirmed Successfully for Booking ID: " + bookingId);

            return ResponseEntity.ok("Webhook received");
        } catch (Exception e) {
            System.err.println("❌ WEBHOOK ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid Webhook: " + e.getMessage());
        }
    }

    @GetMapping({ "", "/payos-webhook" })
    public ResponseEntity<String> checkWebhookHealth() {
        return ResponseEntity.ok("Webhook endpoint is active. Use POST for webhooks.");
    }

    @GetMapping("/debug-config")
    public ResponseEntity<String> debugConfig() {
        return ResponseEntity.ok(
                "Client ID: "
                        + (debugClientId != null && debugClientId.length() > 5 ? debugClientId.substring(0, 5) + "***"
                                : debugClientId)
                        + "\n" +
                        "API Key: "
                        + (debugApiKey != null && debugApiKey.length() > 5 ? debugApiKey.substring(0, 5) + "***"
                                : debugApiKey)
                        + "\n" +
                        "Checksum Key: "
                        + (debugChecksumKey != null && debugChecksumKey.length() > 5
                                ? debugChecksumKey.substring(0, 5) + "***"
                                : debugChecksumKey));
    }

    @GetMapping("/check-status/{orderCode}")
    public ApiResponse<String> checkPaymentStatus(@PathVariable Long orderCode) {
        try {
            // Check status từ Database (đã được Webhook cập nhật)
            BookingResponse booking = bookingService.getBookingById(orderCode);

            String status = "PENDING";
            if (Boolean.TRUE.equals(booking.getIsPaid()) || booking.getStatus() == BookingStatus.CONFIRMED) {
                status = "PAID";
            } else if (booking.getStatus() == BookingStatus.CANCELLED) {
                status = "CANCELLED";
            }

            return ApiResponse.<String>builder()
                    .result(status)
                    .message("Check status thành công")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi check status: " + e.getMessage());
        }
    }
}
