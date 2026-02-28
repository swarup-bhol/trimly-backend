package com.trimly.repository;

import com.trimly.entity.Booking;
import com.trimly.entity.Shop;
import com.trimly.entity.User;
import com.trimly.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByShopOrderByCreatedAtDesc(Shop shop);
    List<Booking> findByCustomerOrderByCreatedAtDesc(User customer);
    List<Booking> findByShopAndStatus(Shop shop, BookingStatus status);
    List<Booking> findByShopAndBookingDate(Shop shop, String bookingDate);

    @Query("SELECT b FROM Booking b WHERE b.shop = :shop AND b.bookingDate = :date AND b.status NOT IN ('REJECTED','CANCELLED')")
    List<Booking> findActiveByShopAndDate(@Param("shop") Shop shop, @Param("date") String date);

    long countByStatus(BookingStatus status);

    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Booking b WHERE b.status = 'COMPLETED'")
    Double sumCompletedRevenue();

    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Booking b WHERE b.shop = :shop AND b.status = 'COMPLETED'")
    Double sumRevenueByShop(@Param("shop") Shop shop);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.shop = :shop AND b.status = 'COMPLETED'")
    long countCompletedByShop(@Param("shop") Shop shop);
}
