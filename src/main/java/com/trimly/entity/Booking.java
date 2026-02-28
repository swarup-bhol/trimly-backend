package com.trimly.entity;

import com.trimly.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User customer;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_phone")
    private String customerPhone;

    // Comma-separated service IDs
    @Column(name = "service_ids")
    private String serviceIds;

    // Human-readable service names
    @Column(name = "services_label")
    private String servicesLabel;

    @Column(nullable = false)
    private String slot;

    @Column(name = "slot_id")
    private String slotId;

    @Column(name = "booking_date")
    private String bookingDate;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private Integer duration;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    private Integer rating;

    @Column(name = "rating_comment")
    private String ratingComment;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
