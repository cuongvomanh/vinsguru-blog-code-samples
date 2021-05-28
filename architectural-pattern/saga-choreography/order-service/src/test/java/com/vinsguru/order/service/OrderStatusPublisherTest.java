package com.vinsguru.order.service;

import com.vinsguru.dto.OrderRequestDto;
import com.vinsguru.events.order.OrderEvent;
import com.vinsguru.events.order.OrderStatus;
import com.vinsguru.order.entity.PurchaseOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Sinks;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderStatusPublisherTest {

    @Mock
    private Sinks.Many<OrderEvent> orderSink;

    @InjectMocks
    private OrderStatusPublisher orderStatusPublisher;

    private static PurchaseOrder purchaseOrder = new PurchaseOrder();

    @BeforeEach
    private void init(){
        purchaseOrder.setProductId(1);
        purchaseOrder.setUserId(1);
    }

    @Test
    public void testRaiseOderEvent(){
        when(orderSink.tryEmitNext(Mockito.any())).thenReturn(null);
        assertAll(() -> orderStatusPublisher.raiseOrderEvent(purchaseOrder, OrderStatus.ORDER_CREATED));
    }

    @Test
    public void testRaiseOderEvent_whenTryEmitNextFailed(){
        when(orderSink.tryEmitNext(Mockito.any())).thenThrow(new RuntimeException());
        assertThrows(Exception.class, () -> orderStatusPublisher.raiseOrderEvent(purchaseOrder, OrderStatus.ORDER_CREATED));
    }
}
