package com.trimly.controller;

import com.trimly.dto.*;
import com.trimly.enums.ShopStatus;
import com.trimly.service.ShopService;
import com.trimly.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;
    private final SecurityUtils securityUtils;

    // ─── Public ──────────────────────────────────────────────────────────────
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<List<ShopDto.ShopResponse>>> getPublicShops() {
        return ResponseEntity.ok(ApiResponse.ok(shopService.getPublicShops()));
    }

    @GetMapping("/public/{shopId}")
    public ResponseEntity<ApiResponse<ShopDto.ShopResponse>> getPublicShop(@PathVariable Long shopId) {
        return ResponseEntity.ok(ApiResponse.ok(shopService.getPublicShop(shopId)));
    }

    @GetMapping("/{shopId}/services")
    public ResponseEntity<ApiResponse<List<ServiceDto.ServiceResponse>>> getShopServices(@PathVariable Long shopId) {
        return ResponseEntity.ok(ApiResponse.ok(shopService.getShopServices(shopId)));
    }

    @GetMapping("/{shopId}/slots")
    public ResponseEntity<ApiResponse<List<ShopDto.SlotResponse>>> getShopSlots(
            @PathVariable Long shopId,
            @RequestParam(defaultValue = "today") String date) {
        return ResponseEntity.ok(ApiResponse.ok(shopService.getShopSlots(shopId, date)));
    }

    // ─── Barber ──────────────────────────────────────────────────────────────
    @GetMapping("/my")
    @PreAuthorize("hasRole('BARBER')")
    public ResponseEntity<ApiResponse<ShopDto.ShopResponse>> getMyShop() {
        return ResponseEntity.ok(ApiResponse.ok(shopService.getMyShop(securityUtils.getCurrentUser())));
    }

    @PutMapping("/my")
    @PreAuthorize("hasRole('BARBER')")
    public ResponseEntity<ApiResponse<ShopDto.ShopResponse>> updateMyShop(
            @RequestBody ShopDto.UpdateShopRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Shop updated", shopService.updateMyShop(securityUtils.getCurrentUser(), req)));
    }

    @PostMapping("/my/services")
    @PreAuthorize("hasRole('BARBER')")
    public ResponseEntity<ApiResponse<ServiceDto.ServiceResponse>> addService(
            @Valid @RequestBody ServiceDto.CreateServiceRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Service added", shopService.addService(securityUtils.getCurrentUser(), req)));
    }

    @PutMapping("/my/services/{serviceId}")
    @PreAuthorize("hasRole('BARBER')")
    public ResponseEntity<ApiResponse<ServiceDto.ServiceResponse>> updateService(
            @PathVariable Long serviceId,
            @RequestBody ServiceDto.UpdateServiceRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Service updated", shopService.updateService(securityUtils.getCurrentUser(), serviceId, req)));
    }

    @DeleteMapping("/my/services/{serviceId}")
    @PreAuthorize("hasRole('BARBER')")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Long serviceId) {
        shopService.deleteService(securityUtils.getCurrentUser(), serviceId);
        return ResponseEntity.ok(ApiResponse.ok("Service deleted", null));
    }

    // ─── Admin ────────────────────────────────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ShopDto.ShopResponse>>> getAllShops() {
        return ResponseEntity.ok(ApiResponse.ok(shopService.getAllShops()));
    }

    @PatchMapping("/{shopId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShopDto.ShopResponse>> updateShopStatus(
            @PathVariable Long shopId,
            @RequestParam String status) {
        ShopStatus shopStatus;
        try { shopStatus = ShopStatus.valueOf(status.toUpperCase()); }
        catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Invalid status")); }
        return ResponseEntity.ok(ApiResponse.ok("Status updated", shopService.updateShopStatus(shopId, shopStatus)));
    }
}
