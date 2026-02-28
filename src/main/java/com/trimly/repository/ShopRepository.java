package com.trimly.repository;

import com.trimly.entity.Shop;
import com.trimly.entity.User;
import com.trimly.enums.ShopStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByOwner(User owner);
    List<Shop> findByStatus(ShopStatus status);
    List<Shop> findByStatusNot(ShopStatus status);

    @Query("SELECT s FROM Shop s WHERE s.status = 'ACTIVE' AND s.isOpen = true")
    List<Shop> findOpenShops();

    @Query("SELECT s FROM Shop s WHERE s.status = 'ACTIVE'")
    List<Shop> findActiveShops();

    long countByStatus(ShopStatus status);
}
