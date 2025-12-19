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
@PreAuthorize("hasRole('USER')")
public class PaymentController {

    @Autowired
    private PayOS payOS;

    @Autowired
    private BookingService bookingService;

    @Value("${app.frontend.url:https://tripgo-frontend.vercel.app}")
    private String frontendBaseUrl;

    // URL của backend để PayOS callback về
    @Value("${app.backend.url:https://tripgo-api.onrender.com}")
    private String backendBaseUrl;

    /**
     * Tạo link thanh toán PayOS và trả về QR code + thông tin chuyển khoản
     */
    @PostMapping("/create-payment-link")
    public ApiResponse<PaymentLinkResponse> createPaymentLink(@RequestBody BookingRequest request) {
        // 1. Tạo Booking trong DB trước
        Booking booking = bookingService.createBooking(request);
        long bookingId = booking.getId(); // Dùng ID làm orderCode cho PayOS

        // 2. Lấy số tiền từ booking (finalPrice đã tính discount)
        // TODO: Bỏ hardcode sau khi test xong
        long amount = 2000; // Test với 2000 đồng
        // long amount = booking.getFinalPrice() != null
        // ? booking.getFinalPrice().longValue()
        // : booking.getTotalPrice().longValue();

        try {
            // 3. Tạo request gửi sang PayOS
            CreatePaymentLinkRequest paymentRequest = CreatePaymentLinkRequest.builder()
                    .orderCode(bookingId)
                    .amount(amount)
                    .description("DH" + bookingId)
                    .returnUrl(backendBaseUrl + "/api/payment/success")
                    .cancelUrl(backendBaseUrl + "/api/payment/cancel")
                    .build();

            // 4. Gọi PayOS tạo link thanh toán
            CreatePaymentLinkResponse response = payOS.paymentRequests().create(paymentRequest);

            // 5. Tạo response với đầy đủ thông tin để hiển thị trên UI
            PaymentLinkResponse paymentInfo = PaymentLinkResponse.builder()
                    .orderCode(bookingId)
                    .amount(amount)
                    .description("DH" + bookingId)
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

        // Cập nhật booking trong DB nếu status = PAID
        if ("PAID".equals(status)) {
            bookingService.confirmPayment(orderCode);
            System.out.println("✅ Thanh toán thành công! Mã đơn: " + orderCode);
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
    @PostMapping("/payos-webhook")
    public ResponseEntity<String> handlePayOSWebhook(@RequestBody Webhook webhookBody) {
        try {
            // Xác thực webhook
            WebhookData data = payOS.webhooks().verify(webhookBody);
            long bookingId = data.getOrderCode();

            // Cập nhật booking status = CONFIRMED, isPaid = true
            bookingService.confirmPayment(bookingId);

            System.out.println("✅ Webhook: Đã nhận tiền đơn hàng: " + bookingId);

            return ResponseEntity.ok("Webhook received");
        } catch (Exception e) {
            System.err.println("❌ Webhook error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid Webhook: " + e.getMessage());
        }
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
