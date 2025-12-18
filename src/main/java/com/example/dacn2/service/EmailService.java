package com.example.dacn2.service;

import com.example.dacn2.entity.booking.Booking;
import com.example.dacn2.entity.booking.BookingType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from:TripGo <noreply@tripgo.com>}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    /**
     * Gửi email xác nhận đặt phòng (sau khi tạo booking)
     */
    @Async
    public void sendBookingConfirmationEmail(Booking booking) {
        try {
            Context context = createBookingContext(booking);
            context.setVariable("emailType", "CONFIRMATION");
            context.setVariable("title", "Xác nhận đặt phòng");
            context.setVariable("message", "Cảm ơn bạn đã đặt phòng tại TripGo! Dưới đây là thông tin chi tiết:");

            String htmlContent = templateEngine.process("email/booking-email", context);
            sendHtmlEmail(booking.getContactEmail(), "TripGo - Xác nhận đặt phòng #" + booking.getBookingCode(),
                    htmlContent);

            log.info("✅ Sent booking confirmation email to: {}", booking.getContactEmail());
        } catch (Exception e) {
            log.error("❌ Failed to send booking confirmation email: {}", e.getMessage());
        }
    }

    /**
     * Gửi email thanh toán thành công
     */
    @Async
    public void sendPaymentSuccessEmail(Booking booking) {
        try {
            Context context = createBookingContext(booking);
            context.setVariable("emailType", "PAYMENT_SUCCESS");
            context.setVariable("title", "Thanh toán thành công");
            context.setVariable("message", "Thanh toán của bạn đã được xác nhận. Đơn hàng đã hoàn tất!");

            String htmlContent = templateEngine.process("email/booking-email", context);
            sendHtmlEmail(booking.getContactEmail(), "TripGo - Thanh toán thành công #" + booking.getBookingCode(),
                    htmlContent);

            log.info("✅ Sent payment success email to: {}", booking.getContactEmail());
        } catch (Exception e) {
            log.error("❌ Failed to send payment success email: {}", e.getMessage());
        }
    }

    /**
     * Gửi email thông báo hủy đặt phòng
     */
    @Async
    public void sendBookingCancellationEmail(Booking booking) {
        try {
            Context context = createBookingContext(booking);
            context.setVariable("emailType", "CANCELLATION");
            context.setVariable("title", "Đã hủy đặt phòng");
            context.setVariable("message", "Đơn hàng của bạn đã được hủy thành công.");

            String htmlContent = templateEngine.process("email/booking-email", context);
            sendHtmlEmail(booking.getContactEmail(), "TripGo - Đã hủy đơn hàng #" + booking.getBookingCode(),
                    htmlContent);

            log.info("✅ Sent cancellation email to: {}", booking.getContactEmail());
        } catch (Exception e) {
            log.error("❌ Failed to send cancellation email: {}", e.getMessage());
        }
    }

    /**
     * Tạo context chung cho tất cả email booking
     */
    private Context createBookingContext(Booking booking) {
        Context context = new Context();

        // Thông tin cơ bản
        context.setVariable("bookingCode", booking.getBookingCode());
        context.setVariable("contactName", booking.getContactName());
        context.setVariable("contactEmail", booking.getContactEmail());
        context.setVariable("contactPhone", booking.getContactPhone());
        context.setVariable("bookingType", booking.getType() != null ? booking.getType().name() : "HOTEL");

        // Format tiền
        context.setVariable("totalPrice", formatCurrency(booking.getTotalPrice()));
        context.setVariable("discountAmount", formatCurrency(booking.getDiscountAmount()));
        context.setVariable("finalPrice", formatCurrency(booking.getFinalPrice()));

        // Thông tin chi tiết theo loại booking
        try {
            if (booking.getType() == BookingType.HOTEL || booking.getRoom() != null) {
                if (booking.getRoom() != null && booking.getRoom().getHotel() != null) {
                    context.setVariable("serviceName", booking.getRoom().getHotel().getName());
                    context.setVariable("serviceDetail", booking.getRoom().getName());
                } else {
                    context.setVariable("serviceName", "Khách sạn");
                    context.setVariable("serviceDetail", "");
                }
                context.setVariable("checkInDate", formatDate(booking.getCheckInDate()));
                context.setVariable("checkOutDate", formatDate(booking.getCheckOutDate()));
                context.setVariable("quantity", booking.getQuantity() + " phòng");
            } else if (booking.getType() == BookingType.FLIGHT && booking.getFlight() != null) {
                context.setVariable("serviceName", "Chuyến bay: " + booking.getFlight().getFlightNumber());
                context.setVariable("serviceDetail",
                        booking.getFlightSeat() != null ? booking.getFlightSeat().getSeatClass() : "");
                context.setVariable("checkInDate", formatDate(booking.getFlight().getDepartureTime()));
                context.setVariable("checkOutDate", formatDate(booking.getFlight().getArrivalTime()));
                context.setVariable("quantity", booking.getQuantity() + " vé");
            } else if (booking.getType() == BookingType.TOUR && booking.getTour() != null) {
                context.setVariable("serviceName", booking.getTour().getTitle());
                context.setVariable("serviceDetail",
                        booking.getTourSchedule() != null
                                ? "Khởi hành: " + formatDate(booking.getTourSchedule().getStartDate().atStartOfDay())
                                : "");
                context.setVariable("checkInDate", "");
                context.setVariable("checkOutDate", "");
                context.setVariable("quantity", booking.getQuantity() + " người");
            } else {
                // Fallback
                context.setVariable("serviceName", "Dịch vụ TripGo");
                context.setVariable("serviceDetail", "");
                context.setVariable("checkInDate", "");
                context.setVariable("checkOutDate", "");
                context.setVariable("quantity", booking.getQuantity() + "");
            }
        } catch (Exception e) {
            log.warn("Could not load service details for email: {}", e.getMessage());
            context.setVariable("serviceName", "Dịch vụ TripGo");
            context.setVariable("serviceDetail", "");
            context.setVariable("checkInDate", "");
            context.setVariable("checkOutDate", "");
            context.setVariable("quantity", booking.getQuantity() + "");
        }

        // Voucher info
        if (booking.getVoucher() != null) {
            context.setVariable("voucherCode", booking.getVoucher().getCode());
        }

        // Links
        context.setVariable("frontendUrl", frontendUrl);
        context.setVariable("bookingDetailUrl", frontendUrl + "/booking/" + booking.getBookingCode());

        return context;
    }

    /**
     * Gửi email HTML
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private String formatCurrency(Double amount) {
        if (amount == null)
            return "0 ₫";
        return CURRENCY_FORMATTER.format(amount);
    }

    private String formatDate(java.time.LocalDateTime dateTime) {
        if (dateTime == null)
            return "";
        return dateTime.format(DATE_FORMATTER);
    }
}
