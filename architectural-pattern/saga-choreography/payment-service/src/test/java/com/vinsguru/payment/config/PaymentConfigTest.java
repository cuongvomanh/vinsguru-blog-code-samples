package com.vinsguru.payment.config;

import com.vinsguru.dto.PurchaseOrderDto;
import com.vinsguru.events.order.OrderEvent;
import com.vinsguru.events.order.OrderStatus;
import com.vinsguru.events.payment.PaymentEvent;
import com.vinsguru.events.payment.PaymentStatus;
import com.vinsguru.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentConfigTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentConfig paymentConfig;

    private static OrderEvent orderEvent;
    private static OrderStatus orderStatus;
    private static PurchaseOrderDto purchaseOrderDto;

    @BeforeEach
    public void init(){
        orderEvent = new OrderEvent();
        purchaseOrderDto = new PurchaseOrderDto(null,1, 1,1);
        orderEvent.setPurchaseOrder(purchaseOrderDto);
    }

    @Test
    public void processPaymentTest(){
        orderEvent.setOrderStatus(OrderStatus.ORDER_CREATED);
        PaymentEvent paymentEvent = new PaymentEvent();
        paymentEvent.setPaymentStatus(PaymentStatus.RESERVED);
        when(paymentService.newOrderEvent(Mockito.any())).thenReturn(paymentEvent);
        Mono<PaymentEvent> paymentEventMono = paymentConfig.processPayment(orderEvent);
        assertEquals(paymentEventMono.map(e -> e.getPaymentStatus()).block(), PaymentStatus.RESERVED);
    }

    @Test
    public void processPaymentTest_whenPaymentStatusReject(){
        orderEvent.setOrderStatus(OrderStatus.ORDER_CREATED);
        PaymentEvent paymentEvent = new PaymentEvent();
        paymentEvent.setPaymentStatus(PaymentStatus.REJECTED);
        when(paymentService.newOrderEvent(Mockito.any())).thenReturn(paymentEvent);
        Mono<PaymentEvent> paymentEventMono = paymentConfig.processPayment(orderEvent);
        assertEquals(paymentEventMono.map(e -> e.getPaymentStatus()).block(), PaymentStatus.REJECTED);
    }

    @Test
    public void processPaymentTest_whenCallNewOrderEventException(){
        orderEvent.setOrderStatus(OrderStatus.ORDER_CREATED);
        PaymentEvent paymentEvent = new PaymentEvent();
        paymentEvent.setPaymentStatus(PaymentStatus.RESERVED);
        when(paymentService.newOrderEvent(Mockito.any())).thenThrow(new RuntimeException());
        Mono<PaymentEvent> paymentEventMono = paymentConfig.processPayment(orderEvent);
        assertThrows(Exception.class, () -> paymentEventMono.block());
    }

    @Test
    public void processPaymentTest_whenOrderStatusCancel(){
        orderEvent.setOrderStatus(OrderStatus.ORDER_CANCELLED);
        doNothing().when(paymentService).cancelOrderEvent(Mockito.any());
        Mono<PaymentEvent> paymentEventMono = paymentConfig.processPayment(orderEvent);
        assertNull(paymentEventMono.block());
    }

    @Test
    public void processPaymentTest_whenOrderStatusComplete(){
        orderEvent.setOrderStatus(OrderStatus.ORDER_COMPLETED);
        doNothing().when(paymentService).cancelOrderEvent(Mockito.any());
        Mono<PaymentEvent> paymentEventMono = paymentConfig.processPayment(orderEvent);
        assertNull(paymentEventMono.block());
    }

    @Test
    public void processPaymentTest_whenOrderStatusNull(){
        assertThrows(Exception.class, () -> paymentConfig.processPayment(orderEvent));
    }

    @Test
    public void processPaymentTest_whenCallCancelOrderEventException(){
        orderEvent.setOrderStatus(OrderStatus.ORDER_CANCELLED);
        PaymentEvent paymentEvent = new PaymentEvent();
        paymentEvent.setPaymentStatus(PaymentStatus.RESERVED);
        doThrow(new RuntimeException()).when(paymentService).cancelOrderEvent(Mockito.any());
        Mono<PaymentEvent> paymentEventMono = paymentConfig.processPayment(orderEvent);
        assertThrows(Exception.class, () -> paymentEventMono.block());
    }




}
