package com.trimly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class ServiceDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceResponse {
        private Long id;
        private String name;
        private String description;
        private String category;
        private String icon;
        private Integer duration;
        private Double price;
        private Boolean enabled;
    }

    @Data
    public static class CreateServiceRequest {
        @NotBlank
        private String name;
        private String description;
        private String category;
        private String icon;
        @NotNull
        private Integer duration;
        @NotNull
        private Double price;
        private Boolean enabled = true;
    }

    @Data
    public static class UpdateServiceRequest {
        private String name;
        private String description;
        private String category;
        private String icon;
        private Integer duration;
        private Double price;
        private Boolean enabled;
    }
}
