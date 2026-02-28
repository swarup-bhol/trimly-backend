package com.trimly.repository;

import com.trimly.entity.Service;
import com.trimly.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByShop(Shop shop);
    List<Service> findByShopAndEnabled(Shop shop, Boolean enabled);
    void deleteByShop(Shop shop);
}
