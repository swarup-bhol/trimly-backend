package com.trimly.controller;

import com.trimly.dto.*;
import com.trimly.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shops/public")
@RequiredArgsConstructor
public class ShopPublicController {

    private final ShopService shopService;

    /** Browse active shops — optional filters: q (search text), city, area */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ShopResponse>>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String area) {
        return ResponseEntity.ok(ApiResponse.ok(shopService.getPublicShops(q, city, area)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShopResponse>> byId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(shopService.getPublicShopById(id)));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ShopResponse>> bySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.ok(shopService.getPublicShopBySlug(slug)));
    }
}
