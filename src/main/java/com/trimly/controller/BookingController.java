package com.trimly.controller;

import com.trimly.dto.ApiResponse;
import com.trimly.dto.BookingDto;
import com.trimly.service.BookingService;
import com.trimly.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final SecurityUtils securityUtils;

    // ─── Customer ─────────────────────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<BookingDto.BookingResponse>> createBooking(
            @Valid @RequestBody BookingDto.CreateBookingRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Booking created!", bookingService.createBooking(securityUtils.getCurrentUser(), req)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<BookingDto.BookingResponse>>> getMyBookings() {
        return ResponseEntity.ok(ApiResponse.ok(bookingService.getMyBookings(securityUtils.getCurrentUser())));
    }

    @PatchMapping("/my/{bookingId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<BookingDto.BookingResponse>> cancelBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(ApiResponse.ok("Booking cancelled", bookingService.cancelBooking(securityUtils.getCurrentUser(), bookingId)));
    }

    @PostMapping("/my/{bookingId}/rate")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<BookingDto.BookingResponse>> rateBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody BookingDto.RateBookingRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Rating submitted!", bookingService.rateBooking(securityUtils.getCurrentUser(), bookingId, req)));
    }

    // ─── Barber ───────────────────────────────────────────────────────────────
    @GetMapping("/shop")
    @PreAuthorize("hasRole('BARBER')")
    public ResponseEntity<ApiResponse<List<BookingDto.BookingResponse>>> getShopBookings() {
        return ResponseEntity.ok(ApiResponse.ok(bookingService.getShopBookings(securityUtils.getCurrentUser())));
    }

    @PatchMapping("/shop/{bookingId}/status")
    @PreAuthorize("hasRole('BARBER')")
    public ResponseEntity<ApiResponse<BookingDto.BookingResponse>> updateBookingStatus(
            @PathVariable Long bookingId,
            @Valid @RequestBody BookingDto.UpdateStatusRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated", bookingService.updateBookingStatus(securityUtils.getCurrentUser(), bookingId, req)));
    }

    // ─── Admin ────────────────────────────────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BookingDto.BookingResponse>>> getAllBookings() {
        return ResponseEntity.ok(ApiResponse.ok(bookingService.getAllBookings()));
    }
}
