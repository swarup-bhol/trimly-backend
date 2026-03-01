package com.trimly.controller;

import com.trimly.dto.*;
import com.trimly.entity.User;
import com.trimly.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final BookingService bookingService;

    /** Create a new booking */
    @PostMapping("/bookings")
    public ResponseEntity<ApiResponse<BookingResponse>> book(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BookingRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(
            "Booking submitted! Barber will confirm shortly.",
            bookingService.create(user.getId(), req)));
    }

    /** Get all bookings for the logged-in customer */
    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<?>> myBookings(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(
            bookingService.getCustomerBookings(user.getId())));
    }

    /** Cancel a pending or confirmed booking */
    @PostMapping("/bookings/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancel(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Booking cancelled",
            bookingService.cancelByCustomer(user.getId(), id)));
    }

    /** Rate and review a completed booking */
    @PostMapping("/bookings/{id}/rate")
    public ResponseEntity<ApiResponse<BookingResponse>> rate(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody RatingRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(
            "Thanks for your review!",
            bookingService.rate(user.getId(), id, req)));
    }

    /**
     * Accept or decline barber's reschedule proposal.
     * Body: { "accept": true/false }
     */
    @PostMapping("/bookings/{id}/reschedule/respond")
    public ResponseEntity<ApiResponse<BookingResponse>> respondReschedule(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody RescheduleResponseRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(
            req.isAccept() ? "Reschedule accepted ✅" : "Declined — original slot kept",
            bookingService.respondToReschedule(user.getId(), id, req)));
    }

    /** Update profile name */
    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<UserInfo>> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok(
            bookingService.updateCustomerProfile(user.getId(), body.get("fullName"))));
    }
}
