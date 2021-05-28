package com.vinsguru.inventory.config;

import com.vinsguru.dto.PurchaseOrderDto;
import com.vinsguru.events.inventory.InventoryEvent;
import com.vinsguru.events.inventory.InventoryStatus;
import com.vinsguru.events.order.OrderEvent;
import com.vinsguru.events.order.OrderStatus;
import com.vinsguru.inventory.entity.OrderInventory;
import com.vinsguru.inventory.service.InventoryService;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryConfigTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryConfig inventoryConfig;

    private static OrderEvent orderEvent;
    private static OrderStatus orderStatus;
    private static PurchaseOrderDto purchaseOrderDto;
    private static Logger LOGGER = LoggerFactory.getLogger(InventoryConfigTest.class);
    private static InventoryEvent inventoryEvent = new InventoryEvent();

    @BeforeEach
    public void init(){
        orderEvent = new OrderEvent();
        purchaseOrderDto = new PurchaseOrderDto(null,1, 1,1);
        orderEvent.setPurchaseOrder(purchaseOrderDto);
    }

    @Test
    public void testProcessInventory(){
        orderStatus = OrderStatus.ORDER_CREATED;
        orderEvent.setOrderStatus(orderStatus);
        inventoryEvent.setStatus(InventoryStatus.RESERVED);
        when(inventoryService.newOrderInventory(Mockito.any())).thenReturn(inventoryEvent);
        Mono<InventoryEvent> inventoryEventMono = inventoryConfig.processInventory(orderEvent);
        assertEquals(inventoryEventMono.map(x -> x.getStatus()).block(), InventoryStatus.RESERVED);
    }

    @Test
    public void testProcessInventory_whenInventoryStatusReject(){
        orderStatus = OrderStatus.ORDER_CREATED;
        orderEvent.setOrderStatus(orderStatus);
        inventoryEvent.setStatus(InventoryStatus.REJECTED);
        when(inventoryService.newOrderInventory(Mockito.any())).thenReturn(inventoryEvent);
        Mono<InventoryEvent> inventoryEventMono = inventoryConfig.processInventory(orderEvent);
        assertEquals(inventoryEventMono.map(x -> x.getStatus()).block(), InventoryStatus.REJECTED);
    }

    @Test
    public void testProcessInventory_whenCallNewOrderInventoryException(){
        orderStatus = OrderStatus.ORDER_CREATED;
        orderEvent.setOrderStatus(orderStatus);
        when(inventoryService.newOrderInventory(Mockito.any())).thenThrow(new RuntimeException());
        Mono<InventoryEvent> inventoryEventMono = inventoryConfig.processInventory(orderEvent);
        assertThrows(Exception.class, () -> inventoryEventMono.block());
    }

    @Test
    public void testProcessInventory_whenOrderStatusIsCancel(){
        orderStatus = OrderStatus.ORDER_CANCELLED;
        orderEvent.setOrderStatus(orderStatus);
        doNothing().when(inventoryService).cancelOrderInventory(Mockito.any());
        Mono<InventoryEvent> inventoryEventMono = inventoryConfig.processInventory(orderEvent);
        assertNull(inventoryEventMono.block());
    }

    @Test
    public void testProcessInventory_whenOrderStatusIsCompleted(){
        orderStatus = OrderStatus.ORDER_COMPLETED;
        orderEvent.setOrderStatus(orderStatus);
        doNothing().when(inventoryService).cancelOrderInventory(Mockito.any());
        Mono<InventoryEvent> inventoryEventMono = inventoryConfig.processInventory(orderEvent);
        assertNull(inventoryEventMono.block());
    }

    @Test
    public void testProcessInventory_whenOrderStatusNull(){
        orderEvent.setOrderStatus(null);
        assertThrows(Exception.class, () -> inventoryConfig.processInventory(orderEvent));
    }

    @Test
    public void testProcessInventory_whenCallCancelOrderInventoryException(){
        orderStatus = OrderStatus.ORDER_CANCELLED;
        orderEvent.setOrderStatus(orderStatus);
        doThrow(RuntimeException.class).when(inventoryService).cancelOrderInventory(Mockito.any());
        Mono<InventoryEvent> inventoryEventMono = inventoryConfig.processInventory(orderEvent);
        assertThrows(Exception.class, () -> inventoryEventMono.block());
    }

}
