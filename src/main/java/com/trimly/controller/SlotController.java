package com.trimly.controller;

import com.trimly.dto.*;
import com.trimly.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class SlotController {

    private final ShopService shopService;

    /**
     * Returns all time slots for a shop on a given date.
     * Each slot shows seat availability — fully booked slots are marked taken.
     * date param: YYYY-MM-DD  (defaults to today if omitted)
     */
    @GetMapping("/{id}/slots")
    public ResponseEntity<ApiResponse<SlotAvailabilityResponse>> slots(
            @PathVariable Long id,
            @RequestParam(required = false) String date) {
        LocalDate d = date != null ? LocalDate.parse(date) : LocalDate.now();
        return ResponseEntity.ok(ApiResponse.ok(shopService.getSlots(id, d)));
    }
}
