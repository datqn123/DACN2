package com.example.dacn2.repository.notification;

import com.example.dacn2.entity.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository để thao tác với bảng notifications
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Lấy danh sách thông báo của user, sắp xếp mới nhất trước
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Lấy top N thông báo mới nhất của user (để hiển thị dropdown)
     */
    List<Notification> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Đếm số thông báo chưa đọc của user
     */
    Long countByUserIdAndIsReadFalse(Long userId);

    /**
     * Đánh dấu tất cả thông báo của user là đã đọc
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Long userId);

    /**
     * Xóa thông báo cũ hơn N ngày (cleanup job)
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < CURRENT_TIMESTAMP - :days DAY")
    int deleteOlderThan(@Param("days") int days);
}
