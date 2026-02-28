package com.trimly.service;

import com.trimly.dto.NotificationDto;
import com.trimly.entity.Notification;
import com.trimly.entity.User;
import com.trimly.exception.ResourceNotFoundException;
import com.trimly.exception.UnauthorizedException;
import com.trimly.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationDto> getMyNotifications(User user) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(User user) {
        return notificationRepository.countUnreadByRecipient(user);
    }

    @Transactional
    public void markRead(User user, Long notifId) {
        Notification notif = notificationRepository.findById(notifId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!notif.getRecipient().getId().equals(user.getId()))
            throw new UnauthorizedException("Not your notification");
        notif.setIsRead(true);
        notificationRepository.save(notif);
    }

    @Transactional
    public void markAllRead(User user) {
        notificationRepository.markAllReadForUser(user);
    }

    private NotificationDto toDto(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .type(n.getType().name())
                .title(n.getTitle())
                .body(n.getBody())
                .isRead(n.getIsRead())
                .bookingId(n.getBooking() != null ? n.getBooking().getId() : null)
                .createdAt(n.getCreatedAt())
                .build();
    }
}
