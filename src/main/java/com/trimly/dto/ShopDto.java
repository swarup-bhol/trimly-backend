package com.trimly.dto;

import com.trimly.enums.ShopStatus;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ShopDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShopResponse {
        private Long id;
        private String ownerName;
        private String shopName;
        private String location;
        private String phone;
        private String bio;
        private String emoji;
        private String color1;
        private String color2;
        private String status;
        private Boolean isOpen;
        private Integer seats;
        private String openTime;
        private String closeTime;
        private Integer slotMin;
        private String workDays;
        private Double rating;
        private Integer reviews;
        private Integer totalBookings;
        private Integer commissionPct;
        private Integer subscriptionFee;
        private String plan;
        private Double monthlyRev;
        private List<ServiceDto.ServiceResponse> services;
        private LocalDateTime createdAt;
    }

    @Data
    public static class UpdateShopRequest {
        private String shopName;
        private String location;
        private String phone;
        private String bio;
        private String emoji;
        private String color1;
        private String color2;
        private Boolean isOpen;
        private Integer seats;
        private String openTime;
        private String closeTime;
        private Integer slotMin;
        private String workDays;
        private String disabledSlots;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SlotResponse {
        private String id;
        private String label;
        private boolean taken;
        private boolean disabled;
    }
}
