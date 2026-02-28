package com.trimly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class BookingDto {

    @Data
    public static class CreateBookingRequest {
        @NotNull
        private Long shopId;
        @NotEmpty
        private List<Long> serviceIds;
        @NotBlank
        private String slot;
        @NotBlank
        private String slotId;
        @NotBlank
        private String bookingDate;
        @NotBlank
        private String customerName;
        private String customerPhone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingResponse {
        private Long id;
        private Long shopId;
        private String shopName;
        private String shopEmoji;
        private String customerName;
        private String customerPhone;
        private List<Long> serviceIds;
        private String servicesLabel;
        private String slot;
        private String slotId;
        private String bookingDate;
        private Double amount;
        private Integer duration;
        private String status;
        private Integer rating;
        private String ratingComment;
        private LocalDateTime createdAt;
    }

    @Data
    public static class UpdateStatusRequest {
        @NotBlank
        private String status;
        private String reason;
    }

    @Data
    public static class RateBookingRequest {
        @NotNull
        private Integer rating;
        private String comment;
    }
}
