package com.example.dacn2.repository.chat;

import com.example.dacn2.entity.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m " +
            "WHERE (m.sender.id = :userId1 AND m.receiver.id = :userId2) " +
            "OR (m.sender.id = :userId2 AND m.receiver.id = :userId1) " +
            "ORDER BY m.timestamp ASC")
    List<Message> findConversation(Long userId1, Long userId2);

    @Query("UPDATE Message m SET m.isRead = true WHERE m.receiver.id = :receiverId AND m.sender.id = :senderId")
    void markAsRead(Long receiverId, Long senderId);
}
