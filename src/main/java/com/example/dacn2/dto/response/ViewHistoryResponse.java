package com.example.dacn2.dto.response;

import com.example.dacn2.entity.User.ViewHistory;
import com.example.dacn2.entity.User.ViewSource;
import com.example.dacn2.entity.hotel.HotelType;
import com.example.dacn2.entity.hotel.PriceRange;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ViewHistoryResponse {
    private Long id;
    private Long hotelId;
    private String hotelName;
    private LocalDateTime viewedAt;
    private Integer viewDurationSeconds;
    private Boolean clickedBooking;
    private Boolean clickedFavorite;
    private ViewSource viewSource;
    private String searchQuery;

    // Hotel snapshot
    private String locationName;
    private Integer hotelStarRating;
    private HotelType hotelType;
    private PriceRange hotelPriceRange;
    private Double hotelPricePerNight;
    private Double hotelAverageRating;

    public static ViewHistoryResponse fromEntity(ViewHistory vh) {
        return ViewHistoryResponse.builder()
                .id(vh.getId())
                .hotelId(vh.getHotel() != null ? vh.getHotel().getId() : null)
                .hotelName(vh.getHotel() != null ? vh.getHotel().getName() : null)
                .viewedAt(vh.getViewedAt())
                .viewDurationSeconds(vh.getViewDurationSeconds())
                .clickedBooking(vh.getClickedBooking())
                .clickedFavorite(vh.getClickedFavorite())
                .viewSource(vh.getViewSource())
                .searchQuery(vh.getSearchQuery())
                .locationName(vh.getLocation() != null ? vh.getLocation().getName() : null)
                .hotelStarRating(vh.getHotelStarRating())
                .hotelType(vh.getHotelType())
                .hotelPriceRange(vh.getHotelPriceRange())
                .hotelPricePerNight(vh.getHotelPricePerNight())
                .hotelAverageRating(vh.getHotelAverageRating())
                .build();
    }
}
