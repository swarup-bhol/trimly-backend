package com.trimly.controller;

import com.trimly.dto.ApiResponse;
import com.trimly.dto.NotificationDto;
import com.trimly.service.NotificationService;
import com.trimly.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getMyNotifications() {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getMyNotifications(securityUtils.getCurrentUser())));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount() {
        long count = notificationService.getUnreadCount(securityUtils.getCurrentUser());
        return ResponseEntity.ok(ApiResponse.ok(Map.of("count", count)));
    }

    @PatchMapping("/{notifId}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(@PathVariable Long notifId) {
        notificationService.markRead(securityUtils.getCurrentUser(), notifId);
        return ResponseEntity.ok(ApiResponse.ok("Marked as read", null));
    }

    @PostMapping("/mark-all-read")
    public ResponseEntity<ApiResponse<Void>> markAllRead() {
        notificationService.markAllRead(securityUtils.getCurrentUser());
        return ResponseEntity.ok(ApiResponse.ok("All marked as read", null));
    }
}
