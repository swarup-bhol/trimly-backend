package com.trimly.dto;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsDto {
    private long totalShops;
    private long activeShops;
    private long pendingShops;
    private long totalBookings;
    private long pendingBookings;
    private long completedBookings;
    private double totalRevenue;
    private double platformRevenue;
    private long totalCustomers;
    private List<ShopDto.ShopResponse> recentShops;
}
