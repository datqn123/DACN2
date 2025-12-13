package com.example.dacn2.service.page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.dacn2.dto.request.BookingRequest;
import com.example.dacn2.dto.request.PassengerRequest;
import com.example.dacn2.dto.response.BookingResponse;
import com.example.dacn2.dto.response.VoucherResponse;
import com.example.dacn2.entity.User.Account;
import com.example.dacn2.entity.booking.Booking;
import com.example.dacn2.entity.booking.BookingPassenger;
import com.example.dacn2.entity.booking.BookingStatus;
import com.example.dacn2.entity.booking.BookingType;
import com.example.dacn2.entity.flight.FlightSeat;
import com.example.dacn2.entity.hotel.Room;
import com.example.dacn2.entity.tour.TourSchedule;
import com.example.dacn2.entity.voucher.DiscountType;
import com.example.dacn2.entity.voucher.Voucher;
import com.example.dacn2.repository.AccountRepository;
import com.example.dacn2.repository.BookingRepository;
import com.example.dacn2.repository.PassengerRepository;
import com.example.dacn2.repository.flight.FlightSeatRepository;
import com.example.dacn2.repository.hotel.RoomRepository;
import com.example.dacn2.repository.tour.TourScheduleRepository;
import com.example.dacn2.repository.voucher.VoucherRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private FlightSeatRepository flightSeatRepository;
    @Autowired
    private TourScheduleRepository tourScheduleRepository;

    // Giá cố định cho dịch vụ bổ sung (có thể đưa vào config hoặc DB sau)
    private static final double TRAVEL_INSURANCE_PRICE = 99000; // VNĐ/khách
    private static final double DELAY_INSURANCE_PRICE = 50000; // VNĐ/khách

    public List<VoucherResponse> getVouchersForBooking(Long hotelId, Double totalAmount) {
        // 1. Query tìm voucher phù hợp
        List<Voucher> vouchers = voucherRepository.findVoucherForBooking(
                LocalDateTime.now(), totalAmount, hotelId);

        // 2. Convert sang DTO và tính tiền giảm ước tính
        return vouchers.stream().map(v -> {
            VoucherResponse dto = new VoucherResponse();
            dto.setId(v.getId());
            dto.setCode(v.getCode());
            dto.setName(v.getName());
            dto.setDescription(v.getDescription());
            dto.setDiscountType(v.getDiscountType());
            dto.setDiscountValue(v.getDiscountValue());
            dto.setMaxDiscountAmount(v.getMaxDiscountAmount());
            dto.setMinOrderValue(v.getMinOrderValue());
            dto.setScope(v.getScope());
            dto.setStartDate(v.getStartDate());
            dto.setEndDate(v.getEndDate());
            dto.setIsActive(v.getIsActive());
            dto.setUsageCount(v.getUsageCount());
            dto.setUsageLimit(v.getUsageLimit());
            dto.setUserLimit(v.getUserLimit());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public Booking createBooking(BookingRequest request) {
        // check user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Vui lòng đăng nhập để đặt vé!"));

        Booking booking = new Booking();
        booking.setBookingCode("TGO-" + System.currentTimeMillis()); // Mã đơn hàng ngẫu nhiên
        booking.setUser(user);
        booking.setContactName(request.getContactName());
        booking.setContactPhone(request.getContactPhone());
        booking.setContactEmail(request.getContactEmail());
        booking.setPaymentMethod(request.getPaymentMethod());
        booking.setStatus(BookingStatus.PENDING); // Mới tạo là Chờ thanh toán
        booking.setIsPaid(false);

        // xử lý theo từng service
        double totalPrice = 0;
        switch (request.getType()) {
            case HOTEL -> totalPrice = processHotel(request, booking);
            case FLIGHT -> totalPrice = processFlight(request, booking);
            case TOUR -> totalPrice = processTour(request, booking);
            default -> throw new RuntimeException("Loại dịch vụ không hợp lệ");
        }
        booking.setTotalPrice(totalPrice);
        // nếu user dùng vocuher
        applyVoucher(request.getVoucherCode(), booking, totalPrice);

        // tạo booking để lấy id cho thanh toán
        Booking savedBooking = bookingRepository.save(booking);

        // save passenger
        savePassengers(request.getPassengers(), savedBooking);
        return savedBooking;
    }

    // xử lý hotel
    private double processHotel(BookingRequest request, Booking booking) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));

        // Validate ngày
        LocalDateTime checkIn = request.getCheckInDate().atStartOfDay();
        LocalDateTime checkOut = request.getCheckOutDate().atStartOfDay();
        if (checkIn.isAfter(checkOut) || checkIn.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Ngày đặt phòng không hợp lệ");
        }

        // --- KIỂM TRA PHÒNG TRỐNG (NÂNG CAO) ---
        // Tổng số phòng của loại này
        int totalRooms = room.getQuantity();
        // Số phòng đã bị người khác đặt trong khoảng thời gian này
        Long bookedRooms = bookingRepository.countBookedRooms(room.getId(), checkIn, checkOut);

        if (totalRooms - bookedRooms <= 0) {
            throw new RuntimeException("Phòng này đã hết chỗ trong ngày bạn chọn!");
        }
        // ---------------------------------------

        booking.setRoom(room);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setQuantity(request.getQuantity()); // Mặc định 1 phòng (nếu muốn đặt nhiều phòng thì cần sửa DTO nhận
                                                    // số lượng)

        // Tính tiền: Giá * Số đêm
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights < 1)
            nights = 1;

        return room.getPrice() * nights;
    }

    // xử lý máy bay
    private double processFlight(BookingRequest request, Booking booking) {
        // kiểm tra ghế
        FlightSeat flightSeat = flightSeatRepository.findById(request.getFlightSeatId())
                .orElseThrow(() -> new RuntimeException("Hạng vé không tồn tại"));

        int quantity = request.getQuantity();
        if (quantity <= 0)
            quantity = 1;

        if (flightSeat.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Hạng vé này chỉ còn " + flightSeat.getAvailableQuantity() + " chỗ!");
        }

        flightSeat.setAvailableQuantity(flightSeat.getAvailableQuantity() - quantity);
        flightSeatRepository.save(flightSeat);

        // lưu các thông tin ghế vào booking
        booking.setFlight(flightSeat.getFlight());
        booking.setFlightSeat(flightSeat);
        booking.setQuantity(quantity);
        booking.setType(BookingType.FLIGHT);

        // tính tiền cho ghế
        double basePrice = flightSeat.getPrice() * quantity;
        double insurancePrice = 0;

        // bảo hiểm chuyến đi
        if (Boolean.TRUE.equals(request.getHasTravelInsurance())) {
            insurancePrice += TRAVEL_INSURANCE_PRICE * quantity;
        }
        // bảo hiểm trễ
        if (Boolean.TRUE.equals(request.getHasDelayInsurance())) {
            insurancePrice += DELAY_INSURANCE_PRICE * quantity;
        }

        // hành lý ký gửi thêm (FE gửi giá lên)
        double baggagePrice = request.getExtraBaggagePrice();

        // trả về total price
        return basePrice + insurancePrice + baggagePrice;
    }

    // xử lý tour
    private double processTour(BookingRequest request, Booking booking) {
        TourSchedule schedule = tourScheduleRepository.findById(request.getTourScheduleId())
                .orElseThrow(() -> new RuntimeException("Lịch trình tour không tồn tại"));

        int quantity = request.getQuantity();
        if (quantity <= 0)
            quantity = 1;

        // Kiểm tra còn chỗ không
        if (schedule.getAvailableSeats() < quantity) {
            throw new RuntimeException("Chuyến tour này chỉ còn " + schedule.getAvailableSeats() + " chỗ!");
        }

        // Giảm số chỗ còn lại
        schedule.setAvailableSeats(schedule.getAvailableSeats() - quantity);
        tourScheduleRepository.save(schedule);

        // Lưu thông tin tour vào booking
        booking.setTour(schedule.getTour());
        booking.setTourSchedule(schedule);
        booking.setQuantity(quantity);
        booking.setType(BookingType.TOUR);

        // Tính tiền: Giá tour * Số người
        Double tourPrice = schedule.getTour().getPrice();
        if (tourPrice == null) {
            tourPrice = schedule.getTour().getPriceAdult(); // Fallback sang giá người lớn
        }
        if (tourPrice == null) {
            throw new RuntimeException("Tour chưa có giá, vui lòng liên hệ admin!");
        }

        return tourPrice * quantity;
    }

    // dùng voucher
    private void applyVoucher(String code, Booking booking, double totalPrice) {
        if (code == null || code.trim().isEmpty()) {
            booking.setDiscountAmount(0.0);
            booking.setFinalPrice(totalPrice);
            return;
        }

        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));

        LocalDateTime now = LocalDateTime.now();
        if (!voucher.getIsActive())
            throw new RuntimeException("Voucher đang bị khóa");
        if (voucher.getUsageCount() >= voucher.getUsageLimit())
            throw new RuntimeException("Voucher đã hết lượt sử dụng");
        if (now.isBefore(voucher.getStartDate()) || now.isAfter(voucher.getEndDate()))
            throw new RuntimeException("Voucher chưa bắt đầu hoặc đã hết hạn");
        if (totalPrice < voucher.getMinOrderValue())
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu để dùng voucher này");

        // tính giảm giá
        double discount = 0;
        if (voucher.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            discount = voucher.getDiscountValue();
        } else {
            discount = totalPrice * (voucher.getDiscountValue() / 100.0);
            if (voucher.getMaxDiscountAmount() != null) {
                discount = Math.min(discount, voucher.getMaxDiscountAmount());
            }
        }

        // tăng lượt dùng voucher
        voucher.setUsageCount(voucher.getUsageCount() + 1);
        voucherRepository.save(voucher);

        booking.setVoucher(voucher);
        booking.setDiscountAmount(discount);
        booking.setFinalPrice(Math.max(0, totalPrice - discount)); // Không được âm tiền
    }

    private void savePassengers(List<PassengerRequest> passengerRequests, Booking booking) {
        if (passengerRequests == null || passengerRequests.isEmpty())
            return;

        List<BookingPassenger> passengers = new ArrayList<>();
        for (PassengerRequest req : passengerRequests) {
            BookingPassenger p = new BookingPassenger();
            p.setFullName(req.getFullName());
            p.setGender(req.getGender());
            p.setNationality(req.getNationality());
            p.setIdNumber(req.getIdNumber());
            p.setPhoneNumber(req.getPhoneNumber());
            p.setPassengerType(req.getType()); // ADULT, CHILD

            // Parse ngày sinh (xử lý chuỗi rỗng)
            if (req.getDob() != null && !req.getDob().isEmpty()) {
                p.setDateOfBirth(LocalDate.parse(req.getDob()));
            }

            p.setBooking(booking);
            passengers.add(p);
        }
        passengerRepository.saveAll(passengers);
    }

    // ========== PAYMENT INTEGRATION ==========

    // xác nhận thanh toán
    @Transactional
    public void confirmPayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + bookingId));

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setIsPaid(true);
        bookingRepository.save(booking);

        System.out.println("✅ Đã xác nhận thanh toán booking ID: " + bookingId);
    }

    /**
     * Lấy danh sách đơn hàng của user đang đăng nhập
     */
    public List<BookingResponse> getMyBookings() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return bookings.stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách khách sạn đã đặt (tất cả booking HOTEL)
     */
    public List<BookingResponse> getMyHotelBookings() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return bookings.stream()
                // Check nếu type = HOTEL hoặc (type = null và có room thì cũng là hotel)
                .filter(b -> b.getType() == BookingType.HOTEL || (b.getType() == null && b.getRoom() != null))
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Tra cứu đơn hàng theo mã booking code
     */
    public BookingResponse lookupByCode(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode);
        if (booking == null) {
            throw new RuntimeException("Không tìm thấy đơn hàng với mã: " + bookingCode);
        }
        return BookingResponse.fromEntity(booking);
    }

    /**
     * Lấy thông tin booking theo ID
     */
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + id));
        return BookingResponse.fromEntity(booking);
    }
}
