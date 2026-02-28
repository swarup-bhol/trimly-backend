package com.trimly.entity;

import com.trimly.enums.ShopStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "shop_name", nullable = false)
    private String shopName;

    @Column(nullable = false)
    private String location;

    private String phone;
    private String bio;
    private String emoji;

    @Column(name = "color1")
    @Builder.Default
    private String color1 = "#1a1200";

    @Column(name = "color2")
    @Builder.Default
    private String color2 = "#0d0d1a";

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ShopStatus status = ShopStatus.PENDING;

    @Column(name = "is_open")
    @Builder.Default
    private Boolean isOpen = false;

    @Builder.Default
    private Integer seats = 2;

    @Column(name = "open_time")
    @Builder.Default
    private String openTime = "09:00";

    @Column(name = "close_time")
    @Builder.Default
    private String closeTime = "19:00";

    @Column(name = "slot_min")
    @Builder.Default
    private Integer slotMin = 30;

    @Column(name = "work_days")
    @Builder.Default
    private String workDays = "Mon,Tue,Wed,Thu,Fri,Sat";

    private Double rating;

    @Builder.Default
    private Integer reviews = 0;

    @Column(name = "total_bookings")
    @Builder.Default
    private Integer totalBookings = 0;

    @Column(name = "commission_pct")
    @Builder.Default
    private Integer commissionPct = 10;

    @Column(name = "subscription_fee")
    @Builder.Default
    private Integer subscriptionFee = 499;

    @Column(name = "plan")
    @Builder.Default
    private String plan = "starter";

    @Column(name = "monthly_rev")
    @Builder.Default
    private Double monthlyRev = 0.0;

    @Column(name = "disabled_slots")
    @Builder.Default
    private String disabledSlots = "";

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Service> services = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
