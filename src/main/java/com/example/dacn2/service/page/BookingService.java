package com.example.dacn2.service.page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dacn2.dto.response.VoucherResponse;
import com.example.dacn2.entity.voucher.Voucher;
import com.example.dacn2.repository.voucher.VoucherRepository;

@Service
public class BookingService {

    @Autowired
    private VoucherRepository voucherRepository;

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
}
