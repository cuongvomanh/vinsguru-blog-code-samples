package com.vinsguru.inventory.service;

import com.vinsguru.dto.InventoryDto;
import com.vinsguru.events.inventory.InventoryEvent;
import com.vinsguru.events.inventory.InventoryStatus;
import com.vinsguru.events.order.OrderEvent;
import com.vinsguru.inventory.entity.OrderInventoryConsumption;
import com.vinsguru.inventory.repository.OrderInventoryConsumptionRepository;
import com.vinsguru.inventory.repository.OrderInventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private static Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);
    @Autowired
    private OrderInventoryRepository inventoryRepository;

    @Autowired
    private OrderInventoryConsumptionRepository consumptionRepository;

    @Transactional
    public InventoryEvent newOrderInventory(OrderEvent orderEvent){
        InventoryDto dto = InventoryDto.of(orderEvent.getPurchaseOrder().getOrderId(), orderEvent.getPurchaseOrder().getProductId());
        InventoryEvent inventoryEvent = inventoryRepository.findById(orderEvent.getPurchaseOrder().getProductId())
                .filter(i -> i.getAvailableInventory() > 0 )
                .map(i -> {
                    i.setAvailableInventory(i.getAvailableInventory() - 1);
                    try {
                        consumptionRepository.save(OrderInventoryConsumption.of(orderEvent.getPurchaseOrder().getOrderId(), orderEvent.getPurchaseOrder().getProductId(), 1));
                        return new InventoryEvent(dto, InventoryStatus.RESERVED);
                    } catch (Exception exception){
                        return null;
                    }
                })
                .orElse(new InventoryEvent(dto, InventoryStatus.REJECTED));
        LOGGER.info("Inventory created");
        return inventoryEvent;
    }

    @Transactional
    public void cancelOrderInventory(OrderEvent orderEvent){
        consumptionRepository.findById(orderEvent.getPurchaseOrder().getOrderId())
                .ifPresent(ci -> {
                    inventoryRepository.findById(ci.getProductId())
                            .ifPresent(i ->
                                i.setAvailableInventory(i.getAvailableInventory() + ci.getQuantityConsumed())
                            );
                    consumptionRepository.delete(ci);
                });
        LOGGER.info("Inventory cancel");
    }

}
