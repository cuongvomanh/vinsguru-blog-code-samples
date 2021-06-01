package com.vinsguru.order.config;

import com.vinsguru.dto.InventoryDto;
import com.vinsguru.events.inventory.InventoryEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class EventHandlesConfigTest {

    @Mock
    private OrderStatusUpdateEventHandler orderEventHandler;

    @InjectMocks
    private EventHandlersConfig eventHandlersConfig;

    @Test
    public void testInventoryEventConsumer(){
        doNothing().when(orderEventHandler).updateOrder(Mockito.any(), Mockito.any());
        Consumer<InventoryEvent> consumer = eventHandlersConfig.inventoryEventConsumer();
        InventoryEvent inventoryEvent = new InventoryEvent();
        InventoryDto inventoryDto = new InventoryDto();
        inventoryEvent.setInventory(inventoryDto);
        assertAll(() -> consumer.accept(inventoryEvent));
    }

    @Test
    public void testInventoryEventConsumer_OrderEventHandlerUpdateOrderFailed(){
        doThrow(new RuntimeException()).when(orderEventHandler).updateOrder(Mockito.any(), Mockito.any());
        Consumer<InventoryEvent> consumer = eventHandlersConfig.inventoryEventConsumer();
        InventoryEvent inventoryEvent = new InventoryEvent();
        InventoryDto inventoryDto = new InventoryDto();
        inventoryEvent.setInventory(inventoryDto);
        assertThrows(Exception.class, () -> consumer.accept(inventoryEvent));
    }
}
