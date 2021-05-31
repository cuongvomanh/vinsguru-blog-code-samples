package com.vinsguru.inventory.service;

import com.vinsguru.dto.InventoryDto;
import com.vinsguru.inventory.entity.OrderInventory;
import com.vinsguru.inventory.repository.OrderInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryQueryService {

    @Autowired
    private OrderInventoryRepository orderInventoryRepository;

    public InventoryDto findByProductId(Integer productId){
        Optional<OrderInventory> orderInventory = orderInventoryRepository.findByProductId(productId);
        orderInventory.orElseThrow(BadRequestCustomException::new);
        return orderInventory.map(e -> InventoryDto.of(null, e.getProductId(), e.getAvailableInventory())).orElseThrow(RuntimeException::new);
    }
}
