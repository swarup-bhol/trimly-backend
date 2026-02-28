package com.trimly.config;

import com.trimly.entity.*;
import com.trimly.enums.BookingStatus;
import com.trimly.enums.Role;
import com.trimly.enums.ShopStatus;
import com.trimly.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ServiceRepository serviceRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping.");
            return;
        }

        log.info("Seeding database with initial data...");

        // ─── Admin ────────────────────────────────────────────────────────────
        User admin = userRepository.save(User.builder()
                .name("Platform Admin")
                .email("admin@trimly.app")
                .passwordHash(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .build());

        // ─── Barber 1 - Blade & Co. ───────────────────────────────────────────
        User barber1 = userRepository.save(User.builder()
                .name("Rajan Sharma")
                .email("rajan@blade.com")
                .phone("+91 98765 43210")
                .passwordHash(passwordEncoder.encode("1234"))
                .role(Role.BARBER)
                .build());

        Shop shop1 = shopRepository.save(Shop.builder()
                .owner(barber1)
                .shopName("Blade & Co.")
                .location("Koramangala, Bangalore")
                .phone("+91 98765 43210")
                .bio("Premium cuts & grooming since 2018. Specialising in classic fades, beard sculpting & modern styles.")
                .emoji("✂️")
                .color1("#1a1200")
                .color2("#0d0d1a")
                .status(ShopStatus.ACTIVE)
                .isOpen(true)
                .seats(3)
                .openTime("09:00")
                .closeTime("20:00")
                .slotMin(30)
                .workDays("Mon,Tue,Wed,Thu,Fri,Sat")
                .rating(4.9)
                .reviews(312)
                .totalBookings(847)
                .plan("pro")
                .subscriptionFee(999)
                .monthlyRev(42000.0)
                .build());

        List<com.trimly.entity.Service> services1 = serviceRepository.saveAll(List.of(
                com.trimly.entity.Service.builder().shop(shop1).name("Classic Haircut").description("Precision scissor/clipper cut").category("hair").duration(30).price(350.0).icon("💇").enabled(true).build(),
                com.trimly.entity.Service.builder().shop(shop1).name("Beard Trim & Shape").description("Define your beard line").category("beard").duration(20).price(200.0).icon("🧔").enabled(true).build(),
                com.trimly.entity.Service.builder().shop(shop1).name("Hot Towel Shave").description("Traditional straight razor").category("facial").duration(40).price(450.0).icon("🧖").enabled(true).build(),
                com.trimly.entity.Service.builder().shop(shop1).name("Modern Fade").description("Skin, low, mid or high fade").category("hair").duration(35).price(400.0).icon("💈").enabled(true).build(),
                com.trimly.entity.Service.builder().shop(shop1).name("Head Massage").description("Scalp & neck relaxation").category("spa").duration(20).price(180.0).icon("💆").enabled(true).build(),
                com.trimly.entity.Service.builder().shop(shop1).name("Hair + Beard Combo").description("Full grooming package").category("combo").duration(50).price(500.0).icon("✨").enabled(true).build(),
                com.trimly.entity.Service.builder().shop(shop1).name("Kids Haircut").description("Fun cuts for little ones").category("kids").duration(25).price(200.0).icon("👶").enabled(true).build()
        ));

        // ─── Barber 2 - The Dapper Den ────────────────────────────────────────
        User barber2 = userRepository.save(User.builder()
                .name("Suresh Pillai")
                .email("suresh@dapper.com")
                .phone("+91 87654 32109")
                .passwordHash(passwordEncoder.encode("1234"))
                .role(Role.BARBER)
                .build());

        Shop shop2 = shopRepository.save(Shop.builder()
                .owner(barber2)
                .shopName("The Dapper Den")
                .location("Indiranagar, Bangalore")
                .phone("+91 87654 32109")
                .bio("Your neighbourhood grooming spot. Quick, clean, precise cuts for the modern man.")
                .emoji("🪒")
                .color1("#0f0a1a")
                .color2("#0d0d1a")
                .status(ShopStatus.ACTIVE)
                .isOpen(true)
                .seats(2)
                .openTime("10:00")
                .closeTime("19:00")
                .slotMin(30)
                .workDays("Mon,Tue,Wed,Thu,Fri,Sat,Sun")
                .rating(4.6)
                .reviews(178)
                .totalBookings(523)
                .plan("starter")
                .subscriptionFee(499)
                .monthlyRev(28000.0)
                .build());

        serviceRepository.saveAll(List.of(
                com.trimly.entity.Service.builder().shop(shop2).name("Modern Fade").description("Any fade style, clean finish").category("hair").duration(45).price(450.0).icon("💈").enabled(true).build(),
                com.trimly.entity.Service.builder().shop(shop2).name("Beard Sculpt").description("Sculpt & define").category("beard").duration(30).price(280.0).icon("🧔").enabled(true).build(),
                com.trimly.entity.Service.builder().shop(shop2).name("Kids Cut").description("Fun cuts").category("kids").duration(20).price(180.0).icon("👶").enabled(true).build(),
                com.trimly.entity.Service.builder().shop(shop2).name("Hair Color").description("Global color or highlights").category("color").duration(60).price(800.0).icon("🎨").enabled(true).build(),
                com.trimly.entity.Service.builder().shop(shop2).name("D-Tan Facial").description("Deep cleanse & brightening").category("facial").duration(45).price(600.0).icon("🧖").enabled(true).build(),
                com.trimly.entity.Service.builder().shop(shop2).name("Fade + Beard").description("Best combo deal").category("combo").duration(75).price(650.0).icon("✨").enabled(true).build()
        ));

        // ─── Barber 3 - Royal Cuts (pending) ─────────────────────────────────
        User barber3 = userRepository.save(User.builder()
                .name("Mohammed Irfan")
                .email("irfan@royalcuts.com")
                .phone("+91 76543 21098")
                .passwordHash(passwordEncoder.encode("1234"))
                .role(Role.BARBER)
                .build());

        Shop shop3 = shopRepository.save(Shop.builder()
                .owner(barber3)
                .shopName("Royal Cuts")
                .location("MG Road, Pune")
                .phone("+91 76543 21098")
                .bio("Luxury grooming experience with premium products and skilled barbers.")
                .emoji("👑")
                .color1("#1a0f00")
                .color2("#0d0a00")
                .status(ShopStatus.PENDING)
                .isOpen(false)
                .seats(4)
                .openTime("09:00")
                .closeTime("21:00")
                .slotMin(30)
                .workDays("Tue,Wed,Thu,Fri,Sat,Sun")
                .rating(0.0)
                .reviews(0)
                .totalBookings(0)
                .plan("pro")
                .subscriptionFee(999)
                .monthlyRev(0.0)
                .build());

        serviceRepository.saveAll(List.of(
                com.trimly.entity.Service.builder().shop(shop3).name("Royal Cut").description("Premium haircut experience").category("hair").duration(45).price(600.0).icon("👑").enabled(true).build(),
                com.trimly.entity.Service.builder().shop(shop3).name("Beard Art").description("Artistic beard sculpting").category("beard").duration(30).price(350.0).icon("🧔").enabled(true).build()
        ));

        // ─── Sample customers ─────────────────────────────────────────────────
        User cust1 = userRepository.save(User.builder()
                .name("Arjun Mehta")
                .email("9876543210@customer.trimly.app")
                .phone("9876543210")
                .passwordHash(passwordEncoder.encode("CUSTOMER_NO_PASSWORD"))
                .role(Role.CUSTOMER)
                .build());

        User cust2 = userRepository.save(User.builder()
                .name("Vikram Das")
                .email("9812345678@customer.trimly.app")
                .phone("9812345678")
                .passwordHash(passwordEncoder.encode("CUSTOMER_NO_PASSWORD"))
                .role(Role.CUSTOMER)
                .build());

        // ─── Sample bookings ──────────────────────────────────────────────────
        String today = java.time.LocalDate.now().toString();
        String yesterday = java.time.LocalDate.now().minusDays(1).toString();

        bookingRepository.save(Booking.builder()
                .shop(shop1).customer(cust1).customerName("Arjun Mehta").customerPhone("9876543210")
                .serviceIds(services1.get(0).getId().toString()).servicesLabel("Classic Haircut")
                .slot("9:30 AM").slotId("930").bookingDate(today)
                .amount(350.0).duration(30).status(BookingStatus.PENDING).build());

        bookingRepository.save(Booking.builder()
                .shop(shop1).customer(cust2).customerName("Vikram Das").customerPhone("9812345678")
                .serviceIds(services1.get(0).getId() + "," + services1.get(1).getId()).servicesLabel("Classic Haircut, Beard Trim & Shape")
                .slot("10:00 AM").slotId("1000").bookingDate(today)
                .amount(550.0).duration(50).status(BookingStatus.CONFIRMED).build());

        bookingRepository.save(Booking.builder()
                .shop(shop1).customer(cust1).customerName("Rohan Kumar").customerPhone("9845671234")
                .serviceIds(services1.get(5).getId().toString()).servicesLabel("Hair + Beard Combo")
                .slot("2:00 PM").slotId("1400").bookingDate(yesterday)
                .amount(500.0).duration(50).status(BookingStatus.COMPLETED).rating(5).build());

        // Update shop1 stats
        shop1.setTotalBookings(3);
        shopRepository.save(shop1);

        log.info("✅ Database seeded successfully!");
        log.info("👤 Admin login: admin@trimly.app / admin123");
        log.info("✂️  Barber 1 login: rajan@blade.com / 1234");
        log.info("🪒 Barber 2 login: suresh@dapper.com / 1234");
        log.info("👑 Barber 3 login (pending): irfan@royalcuts.com / 1234");
        log.info("👤 Customer: name=Arjun Mehta, phone=9876543210");
    }
}
