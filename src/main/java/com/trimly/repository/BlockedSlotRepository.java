package com.trimly.repository;

import com.trimly.entity.BlockedSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface BlockedSlotRepository extends JpaRepository<BlockedSlot, Long> {

    // All blocked slots for a shop on a given date
    @Query("SELECT b.slotTime FROM BlockedSlot b WHERE b.shop.id = :shopId AND b.slotDate = :date")
    List<LocalTime> findBlockedTimes(@Param("shopId") Long shopId, @Param("date") LocalDate date);

    // Find specific blocked slot (for delete)
    @Query("SELECT b FROM BlockedSlot b WHERE b.shop.id = :shopId AND b.slotDate = :date AND b.slotTime = :time")
    Optional<BlockedSlot> findByShopAndDateAndTime(
            @Param("shopId") Long shopId,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time
    );

    // Check if a specific slot is blocked (used in slot availability query)
    @Query("SELECT COUNT(b) > 0 FROM BlockedSlot b WHERE b.shop.id = :shopId AND b.slotDate = :date AND b.slotTime = :time")
    boolean isBlocked(@Param("shopId") Long shopId, @Param("date") LocalDate date, @Param("time") LocalTime time);

    // Delete by shop + date + time directly
    @Query("DELETE FROM BlockedSlot b WHERE b.shop.id = :shopId AND b.slotDate = :date AND b.slotTime = :time")
    @org.springframework.data.jpa.repository.Modifying
    void deleteByShopAndDateAndTime(
            @Param("shopId") Long shopId,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time
    );
}
