package com.trimly.service;

import com.trimly.dto.AdminStatsDto;
import com.trimly.dto.ShopDto;
import com.trimly.enums.BookingStatus;
import com.trimly.enums.Role;
import com.trimly.enums.ShopStatus;
import com.trimly.repository.BookingRepository;
import com.trimly.repository.ShopRepository;
import com.trimly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ShopRepository shopRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ShopService shopService;

    public AdminStatsDto getStats() {
        long totalShops = shopRepository.count();
        long activeShops = shopRepository.countByStatus(ShopStatus.ACTIVE);
        long pendingShops = shopRepository.countByStatus(ShopStatus.PENDING);
        long totalBookings = bookingRepository.count();
        long pendingBookings = bookingRepository.countByStatus(BookingStatus.PENDING);
        long completedBookings = bookingRepository.countByStatus(BookingStatus.COMPLETED);
        double totalRevenue = bookingRepository.sumCompletedRevenue() != null ? bookingRepository.sumCompletedRevenue() : 0;
        double platformRevenue = totalRevenue * 0.10; // 10% commission
        long totalCustomers = userRepository.countByRole(Role.CUSTOMER);

        var recentShops = shopRepository.findByStatus(ShopStatus.PENDING).stream()
                .map(shopService::toResponse)
                .collect(Collectors.toList());

        return AdminStatsDto.builder()
                .totalShops(totalShops)
                .activeShops(activeShops)
                .pendingShops(pendingShops)
                .totalBookings(totalBookings)
                .pendingBookings(pendingBookings)
                .completedBookings(completedBookings)
                .totalRevenue(totalRevenue)
                .platformRevenue(platformRevenue)
                .totalCustomers(totalCustomers)
                .recentShops(recentShops)
                .build();
    }
}
