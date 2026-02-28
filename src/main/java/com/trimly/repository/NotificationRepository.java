package com.trimly.repository;

import com.trimly.entity.Notification;
import com.trimly.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient = :user AND n.isRead = false")
    long countUnreadByRecipient(@Param("user") User user);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipient = :user AND n.isRead = false")
    void markAllReadForUser(@Param("user") User user);
}
