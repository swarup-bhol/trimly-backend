package com.trimly.service;

import com.trimly.dto.ServiceDto;
import com.trimly.dto.ShopDto;
import com.trimly.entity.*;
import com.trimly.enums.BookingStatus;
import com.trimly.enums.ShopStatus;
import com.trimly.exception.BadRequestException;
import com.trimly.exception.ResourceNotFoundException;
import com.trimly.repository.BookingRepository;
import com.trimly.repository.ServiceRepository;
import com.trimly.repository.ShopRepository;
import com.trimly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final ServiceRepository serviceRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    // ─── Public ─────────────────────────────────────────────────────────────
    public List<ShopDto.ShopResponse> getPublicShops() {
        return shopRepository.findActiveShops().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ShopDto.ShopResponse getPublicShop(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        if (shop.getStatus() != ShopStatus.ACTIVE)
            throw new BadRequestException("Shop is not available");
        return toResponse(shop);
    }

    public List<ServiceDto.ServiceResponse> getShopServices(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        return serviceRepository.findByShopAndEnabled(shop, true).stream()
                .map(this::toServiceResponse)
                .collect(Collectors.toList());
    }

    public List<ShopDto.SlotResponse> getShopSlots(Long shopId, String date) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));

        // Get taken slot IDs from active bookings for the date
        List<Booking> activeBookings = bookingRepository.findActiveByShopAndDate(shop, date);
        List<String> takenSlots = activeBookings.stream()
                .map(Booking::getSlotId)
                .collect(Collectors.toList());

        List<String> disabledSlots = shop.getDisabledSlots() != null && !shop.getDisabledSlots().isEmpty()
                ? Arrays.asList(shop.getDisabledSlots().split(","))
                : new ArrayList<>();

        return generateSlots(shop, takenSlots, disabledSlots);
    }

    private List<ShopDto.SlotResponse> generateSlots(Shop shop, List<String> taken, List<String> disabled) {
        List<ShopDto.SlotResponse> slots = new ArrayList<>();
        String[] openParts = shop.getOpenTime().split(":");
        String[] closeParts = shop.getCloseTime().split(":");
        int openMins = Integer.parseInt(openParts[0]) * 60 + Integer.parseInt(openParts[1]);
        int closeMins = Integer.parseInt(closeParts[0]) * 60 + Integer.parseInt(closeParts[1]);
        int slotMin = shop.getSlotMin();

        for (int m = openMins; m < closeMins; m += slotMin) {
            int h = m / 60, min = m % 60;
            String id = h + String.format("%02d", min);
            int displayH = h > 12 ? h - 12 : (h == 0 ? 12 : h);
            String ampm = h >= 12 ? "PM" : "AM";
            String label = displayH + ":" + String.format("%02d", min) + " " + ampm;

            slots.add(ShopDto.SlotResponse.builder()
                    .id(id)
                    .label(label)
                    .taken(taken.contains(id))
                    .disabled(disabled.contains(id))
                    .build());
        }
        return slots;
    }

    // ─── Barber ──────────────────────────────────────────────────────────────
    public ShopDto.ShopResponse getMyShop(User owner) {
        Shop shop = shopRepository.findByOwner(owner)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        return toResponse(shop);
    }

    @Transactional
    public ShopDto.ShopResponse updateMyShop(User owner, ShopDto.UpdateShopRequest req) {
        Shop shop = shopRepository.findByOwner(owner)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));

        if (req.getShopName() != null) shop.setShopName(req.getShopName());
        if (req.getLocation() != null) shop.setLocation(req.getLocation());
        if (req.getPhone() != null) shop.setPhone(req.getPhone());
        if (req.getBio() != null) shop.setBio(req.getBio());
        if (req.getEmoji() != null) shop.setEmoji(req.getEmoji());
        if (req.getColor1() != null) shop.setColor1(req.getColor1());
        if (req.getColor2() != null) shop.setColor2(req.getColor2());
        if (req.getIsOpen() != null) shop.setIsOpen(req.getIsOpen());
        if (req.getSeats() != null) shop.setSeats(req.getSeats());
        if (req.getOpenTime() != null) shop.setOpenTime(req.getOpenTime());
        if (req.getCloseTime() != null) shop.setCloseTime(req.getCloseTime());
        if (req.getSlotMin() != null) shop.setSlotMin(req.getSlotMin());
        if (req.getWorkDays() != null) shop.setWorkDays(req.getWorkDays());
        if (req.getDisabledSlots() != null) shop.setDisabledSlots(req.getDisabledSlots());

        return toResponse(shopRepository.save(shop));
    }

    // ─── Services CRUD (Barber) ──────────────────────────────────────────────
    @Transactional
    public ServiceDto.ServiceResponse addService(User owner, ServiceDto.CreateServiceRequest req) {
        Shop shop = shopRepository.findByOwner(owner)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        com.trimly.entity.Service service = com.trimly.entity.Service.builder()
                .shop(shop)
                .name(req.getName())
                .description(req.getDescription())
                .category(req.getCategory())
                .icon(req.getIcon())
                .duration(req.getDuration())
                .price(req.getPrice())
                .enabled(req.getEnabled() != null ? req.getEnabled() : true)
                .build();
        return toServiceResponse(serviceRepository.save(service));
    }

    @Transactional
    public ServiceDto.ServiceResponse updateService(User owner, Long serviceId, ServiceDto.UpdateServiceRequest req) {
        Shop shop = shopRepository.findByOwner(owner)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        com.trimly.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        if (!service.getShop().getId().equals(shop.getId()))
            throw new BadRequestException("Service does not belong to your shop");

        if (req.getName() != null) service.setName(req.getName());
        if (req.getDescription() != null) service.setDescription(req.getDescription());
        if (req.getCategory() != null) service.setCategory(req.getCategory());
        if (req.getIcon() != null) service.setIcon(req.getIcon());
        if (req.getDuration() != null) service.setDuration(req.getDuration());
        if (req.getPrice() != null) service.setPrice(req.getPrice());
        if (req.getEnabled() != null) service.setEnabled(req.getEnabled());

        return toServiceResponse(serviceRepository.save(service));
    }

    @Transactional
    public void deleteService(User owner, Long serviceId) {
        Shop shop = shopRepository.findByOwner(owner)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        com.trimly.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        if (!service.getShop().getId().equals(shop.getId()))
            throw new BadRequestException("Service does not belong to your shop");
        serviceRepository.delete(service);
    }

    // ─── Admin ────────────────────────────────────────────────────────────────
    public List<ShopDto.ShopResponse> getAllShops() {
        return shopRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShopDto.ShopResponse updateShopStatus(Long shopId, ShopStatus status) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        shop.setStatus(status);
        if (status == ShopStatus.ACTIVE) shop.setIsOpen(true);
        if (status == ShopStatus.DISABLED) shop.setIsOpen(false);
        return toResponse(shopRepository.save(shop));
    }

    // ─── Mappers ──────────────────────────────────────────────────────────────
    public ShopDto.ShopResponse toResponse(Shop shop) {
        List<ServiceDto.ServiceResponse> services = serviceRepository.findByShop(shop).stream()
                .map(this::toServiceResponse)
                .collect(Collectors.toList());

        return ShopDto.ShopResponse.builder()
                .id(shop.getId())
                .ownerName(shop.getOwner().getName())
                .shopName(shop.getShopName())
                .location(shop.getLocation())
                .phone(shop.getPhone())
                .bio(shop.getBio())
                .emoji(shop.getEmoji())
                .color1(shop.getColor1())
                .color2(shop.getColor2())
                .status(shop.getStatus().name())
                .isOpen(shop.getIsOpen())
                .seats(shop.getSeats())
                .openTime(shop.getOpenTime())
                .closeTime(shop.getCloseTime())
                .slotMin(shop.getSlotMin())
                .workDays(shop.getWorkDays())
                .rating(shop.getRating())
                .reviews(shop.getReviews())
                .totalBookings(shop.getTotalBookings())
                .commissionPct(shop.getCommissionPct())
                .subscriptionFee(shop.getSubscriptionFee())
                .plan(shop.getPlan())
                .monthlyRev(shop.getMonthlyRev())
                .services(services)
                .createdAt(shop.getCreatedAt())
                .build();
    }

    public ServiceDto.ServiceResponse toServiceResponse(com.trimly.entity.Service s) {
        return ServiceDto.ServiceResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .category(s.getCategory())
                .icon(s.getIcon())
                .duration(s.getDuration())
                .price(s.getPrice())
                .enabled(s.getEnabled())
                .build();
    }
}
