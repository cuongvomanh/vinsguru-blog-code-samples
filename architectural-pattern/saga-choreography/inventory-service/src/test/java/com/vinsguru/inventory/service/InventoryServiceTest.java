package com.vinsguru.inventory.service;

import com.vinsguru.dto.PurchaseOrderDto;
import com.vinsguru.events.inventory.InventoryEvent;
import com.vinsguru.events.inventory.InventoryStatus;
import com.vinsguru.events.order.OrderEvent;
import com.vinsguru.events.order.OrderStatus;
import com.vinsguru.inventory.entity.OrderInventory;
import com.vinsguru.inventory.entity.OrderInventoryConsumption;
import com.vinsguru.inventory.repository.OrderInventoryConsumptionRepository;
import com.vinsguru.inventory.repository.OrderInventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest{

    @Mock
    private OrderInventoryRepository inventoryRepository;

    @Mock
    private OrderInventoryConsumptionRepository inventoryConsumptionRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private static OrderEvent orderEvent;
    private static OrderStatus orderStatus;
    private static PurchaseOrderDto purchaseOrderDto;
    private static OrderInventory orderInventory;

    @BeforeEach
    public void init(){
        orderEvent = new OrderEvent();
        orderStatus = OrderStatus.ORDER_CREATED;
        orderEvent.setOrderStatus(orderStatus);
        purchaseOrderDto = new PurchaseOrderDto(null,1, 1,1);
        orderEvent.setPurchaseOrder(purchaseOrderDto);
        orderInventory = new OrderInventory();
        orderInventory.setAvailableInventory(1);
    }

    @Test
    public void testCreateInventory(){
        when(inventoryRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(orderInventory));
        when(inventoryConsumptionRepository.save(Mockito.any())).thenReturn(null);
        InventoryEvent inventoryEvent = inventoryService.newOrderInventory(orderEvent);
        assertEquals(inventoryEvent.getStatus(), InventoryStatus.RESERVED);
    }

    @Test
    public void testNewOrderInventory_failFindInventory(){
        when(inventoryRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        InventoryEvent inventoryEvent = inventoryService.newOrderInventory(orderEvent);
        assertEquals(inventoryEvent.getStatus(), InventoryStatus.REJECTED);
    }

    @Test
    public void testNewOrderInventory_failSaveInventoryConsumtion(){
        when(inventoryRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(orderInventory));
        when(inventoryConsumptionRepository.save(Mockito.isA(OrderInventoryConsumption.class))).thenThrow(new RuntimeException());
        InventoryEvent inventoryEvent = inventoryService.newOrderInventory(orderEvent);
        assertEquals(inventoryEvent.getStatus(), InventoryStatus.REJECTED);
    }

    @Test
    public void testCreateInventory_outOfInventory(){
        orderInventory.setAvailableInventory(0);
        when(inventoryRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(orderInventory));
        InventoryEvent inventoryEvent = inventoryService.newOrderInventory(orderEvent);
        assertEquals(inventoryEvent.getStatus(), InventoryStatus.REJECTED);
    }

    @Test
    public void testCancelInventory(){
        OrderInventoryConsumption inventoryConsumption = new OrderInventoryConsumption();
        when(inventoryConsumptionRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(inventoryConsumption));
        doNothing().when(inventoryConsumptionRepository).delete(Mockito.any());
        assertAll(() -> inventoryService.cancelOrderInventory(orderEvent));
    }

    @Test
    public void testCancelInventory_ifNotFoundInventoryConsumption(){
        when(inventoryConsumptionRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        assertAll(() -> inventoryService.cancelOrderInventory(orderEvent));
    }
}