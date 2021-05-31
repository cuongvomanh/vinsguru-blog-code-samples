package com.vinsguru.inventory.repository;

import com.vinsguru.inventory.entity.OrderInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderInventoryRepository extends JpaRepository<OrderInventory, Integer> {
    Optional<OrderInventory> findByProductId(int productId);
}
