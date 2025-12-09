package com.example.dacn2.entity.User;

/**
 * Nguồn mà user đến xem chi tiết hotel
 * Dùng để phân tích hiệu quả của các kênh recommendation
 */
public enum ViewSource {
    SEARCH, // Từ kết quả tìm kiếm
    HOMEPAGE, // Từ gợi ý trang chủ (trending, popular)
    RECOMMENDATION, // Từ gợi ý "Có thể bạn thích" của AI
    FAVORITE, // Từ danh sách yêu thích
    RECENTLY_VIEWED, // Từ "Xem gần đây"
    VOUCHER, // Từ danh sách hotel có voucher
    DIRECT // Truy cập trực tiếp (URL, bookmark)
}
