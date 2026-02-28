package com.trimly.service;

import com.trimly.dto.BookingDto;
import com.trimly.entity.*;
import com.trimly.enums.BookingStatus;
import com.trimly.enums.NotificationType;
import com.trimly.enums.Role;
import com.trimly.enums.ShopStatus;
import com.trimly.exception.BadRequestException;
import com.trimly.exception.ResourceNotFoundException;
import com.trimly.exception.UnauthorizedException;
import com.trimly.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShopRepository shopRepository;
    private final ServiceRepository serviceRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingDto.BookingResponse createBooking(User customer, BookingDto.CreateBookingRequest req) {
        Shop shop = shopRepository.findById(req.getShopId())
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));

        if (shop.getStatus() != ShopStatus.ACTIVE)
            throw new BadRequestException("Shop is not currently active");

        // Validate services belong to this shop
        List<com.trimly.entity.Service> services = serviceRepository.findAllById(req.getServiceIds());
        if (services.size() != req.getServiceIds().size())
            throw new BadRequestException("One or more services not found");
        services.forEach(s -> {
            if (!s.getShop().getId().equals(shop.getId()))
                throw new BadRequestException("Service does not belong to this shop");
            if (!s.getEnabled())
                throw new BadRequestException("Service '" + s.getName() + "' is not available");
        });

        // Check slot not already taken
        List<Booking> existingBookings = bookingRepository.findActiveByShopAndDate(shop, req.getBookingDate());
        boolean slotTaken = existingBookings.stream().anyMatch(b -> b.getSlotId().equals(req.getSlotId()));
        if (slotTaken) throw new BadRequestException("Slot is no longer available. Please choose another.");

        // Calculate totals
        int duration = services.stream().mapToInt(com.trimly.entity.Service::getDuration).sum();
        double amount = services.stream().mapToDouble(com.trimly.entity.Service::getPrice).sum();
        String servicesLabel = services.stream().map(com.trimly.entity.Service::getName).collect(Collectors.joining(", "));
        String serviceIds = req.getServiceIds().stream().map(String::valueOf).collect(Collectors.joining(","));

        Booking booking = Booking.builder()
                .shop(shop)
                .customer(customer)
                .customerName(req.getCustomerName())
                .customerPhone(req.getCustomerPhone())
                .serviceIds(serviceIds)
                .servicesLabel(servicesLabel)
                .slot(req.getSlot())
                .slotId(req.getSlotId())
                .bookingDate(req.getBookingDate())
                .amount(amount)
                .duration(duration)
                .status(BookingStatus.PENDING)
                .build();

        booking = bookingRepository.save(booking);

        // Update shop stats
        shop.setTotalBookings(shop.getTotalBookings() + 1);
        shopRepository.save(shop);

        // Notify the barber
        Notification barberNotif = Notification.builder()
                .recipient(shop.getOwner())
                .recipientRole(Role.BARBER)
                .type(NotificationType.NEW_BOOKING)
                .title("New Booking Request!")
                .body(req.getCustomerName() + " booked " + servicesLabel + " at " + req.getSlot())
                .booking(booking)
                .build();
        notificationRepository.save(barberNotif);

        return toResponse(booking);
    }

    // ─── Customer ─────────────────────────────────────────────────────────────
    public List<BookingDto.BookingResponse> getMyBookings(User customer) {
        return bookingRepository.findByCustomerOrderByCreatedAtDesc(customer).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDto.BookingResponse cancelBooking(User customer, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getCustomer().getId().equals(customer.getId()))
            throw new UnauthorizedException("Not your booking");
        if (booking.getStatus() == BookingStatus.COMPLETED)
            throw new BadRequestException("Cannot cancel a completed booking");
        if (booking.getStatus() == BookingStatus.CANCELLED)
            throw new BadRequestException("Booking is already cancelled");

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Notify barber
        Notification n = Notification.builder()
                .recipient(booking.getShop().getOwner())
                .recipientRole(Role.BARBER)
                .type(NotificationType.BOOKING_CANCELLED)
                .title("Booking Cancelled")
                .body(booking.getCustomerName() + " cancelled their " + booking.getSlot() + " appointment")
                .booking(booking)
                .build();
        notificationRepository.save(n);

        return toResponse(booking);
    }

    @Transactional
    public BookingDto.BookingResponse rateBooking(User customer, Long bookingId, BookingDto.RateBookingRequest req) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getCustomer().getId().equals(customer.getId()))
            throw new UnauthorizedException("Not your booking");
        if (booking.getStatus() != BookingStatus.COMPLETED)
            throw new BadRequestException("Can only rate completed bookings");
        if (booking.getRating() != null && booking.getRating() > 0)
            throw new BadRequestException("Already rated");

        if (req.getRating() < 1 || req.getRating() > 5)
            throw new BadRequestException("Rating must be between 1 and 5");

        booking.setRating(req.getRating());
        booking.setRatingComment(req.getComment());
        bookingRepository.save(booking);

        // Update shop rating
        Shop shop = booking.getShop();
        updateShopRating(shop);

        return toResponse(booking);
    }

    // ─── Barber ───────────────────────────────────────────────────────────────
    public List<BookingDto.BookingResponse> getShopBookings(User owner) {
        Shop shop = shopRepository.findByOwner(owner)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        return bookingRepository.findByShopOrderByCreatedAtDesc(shop).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDto.BookingResponse updateBookingStatus(User owner, Long bookingId, BookingDto.UpdateStatusRequest req) {
        Shop shop = shopRepository.findByOwner(owner)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getShop().getId().equals(shop.getId()))
            throw new UnauthorizedException("Not your booking");

        BookingStatus newStatus;
        try {
            newStatus = BookingStatus.valueOf(req.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + req.getStatus());
        }

        booking.setStatus(newStatus);
        bookingRepository.save(booking);

        // Update monthly revenue if completed
        if (newStatus == BookingStatus.COMPLETED) {
            shop.setMonthlyRev(shop.getMonthlyRev() + booking.getAmount());
            shopRepository.save(shop);
        }

        // Notify customer
        if (booking.getCustomer() != null) {
            NotificationType type = newStatus == BookingStatus.CONFIRMED
                    ? NotificationType.BOOKING_CONFIRMED
                    : newStatus == BookingStatus.REJECTED
                    ? NotificationType.BOOKING_REJECTED
                    : NotificationType.BOOKING_COMPLETED;

            String title = newStatus == BookingStatus.CONFIRMED ? "Booking Confirmed ✓"
                    : newStatus == BookingStatus.REJECTED ? "Booking Rejected"
                    : "Appointment Complete";
            String body = newStatus == BookingStatus.CONFIRMED
                    ? "Your appointment at " + shop.getShopName() + " is confirmed for " + booking.getSlot()
                    : newStatus == BookingStatus.REJECTED
                    ? shop.getShopName() + " cannot accommodate your request. Please book another slot."
                    : "Your appointment at " + shop.getShopName() + " is complete. Please rate your experience!";

            Notification n = Notification.builder()
                    .recipient(booking.getCustomer())
                    .recipientRole(Role.CUSTOMER)
                    .type(type)
                    .title(title)
                    .body(body)
                    .booking(booking)
                    .build();
            notificationRepository.save(n);
        }

        return toResponse(booking);
    }

    // ─── Admin ────────────────────────────────────────────────────────────────
    public List<BookingDto.BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private void updateShopRating(Shop shop) {
        List<Booking> ratedBookings = bookingRepository.findByShopAndStatus(shop, BookingStatus.COMPLETED)
                .stream().filter(b -> b.getRating() != null && b.getRating() > 0).collect(Collectors.toList());
        if (!ratedBookings.isEmpty()) {
            double avg = ratedBookings.stream().mapToInt(Booking::getRating).average().orElse(0);
            shop.setRating(Math.round(avg * 10.0) / 10.0);
            shop.setReviews(ratedBookings.size());
            shopRepository.save(shop);
        }
    }

    public BookingDto.BookingResponse toResponse(Booking b) {
        List<Long> serviceIds = b.getServiceIds() != null && !b.getServiceIds().isEmpty()
                ? Arrays.stream(b.getServiceIds().split(",")).map(Long::parseLong).collect(Collectors.toList())
                : List.of();

        return BookingDto.BookingResponse.builder()
                .id(b.getId())
                .shopId(b.getShop().getId())
                .shopName(b.getShop().getShopName())
                .shopEmoji(b.getShop().getEmoji())
                .customerName(b.getCustomerName())
                .customerPhone(b.getCustomerPhone())
                .serviceIds(serviceIds)
                .servicesLabel(b.getServicesLabel())
                .slot(b.getSlot())
                .slotId(b.getSlotId())
                .bookingDate(b.getBookingDate())
                .amount(b.getAmount())
                .duration(b.getDuration())
                .status(b.getStatus().name())
                .rating(b.getRating())
                .ratingComment(b.getRatingComment())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
