package com.vinsguru.payment.service;

import com.vinsguru.dto.PaymentDto;
import com.vinsguru.dto.PurchaseOrderDto;
import com.vinsguru.events.order.OrderEvent;
import com.vinsguru.events.order.OrderStatus;
import com.vinsguru.events.payment.PaymentEvent;
import com.vinsguru.events.payment.PaymentStatus;
import com.vinsguru.payment.entity.UserBalance;
import com.vinsguru.payment.entity.UserTransaction;
import com.vinsguru.payment.repository.UserBalanceRepository;
import com.vinsguru.payment.repository.UserTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private UserTransactionRepository userTransactionRepository;

    @Mock
    private UserBalanceRepository userBalanceRepository;

    @InjectMocks
    private PaymentService paymentService;

    private static OrderEvent orderEvent;
    private static OrderStatus orderStatus;
    private static PurchaseOrderDto purchaseOrderDto;
    private  static UserBalance userBalance = new UserBalance();

    @BeforeEach
    public void init(){
        orderEvent = new OrderEvent();
        orderStatus = OrderStatus.ORDER_CREATED;
        orderEvent.setOrderStatus(orderStatus);
        purchaseOrderDto = new PurchaseOrderDto(null,1, 1,1);
        orderEvent.setPurchaseOrder(purchaseOrderDto);
    }

    @Test
    public void testNewOrderEvent(){
        UserBalance userBalance = new UserBalance();
        userBalance.setBalance(1);
        when(userBalanceRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(userBalance));
        when(userTransactionRepository.save(Mockito.any())).thenReturn(null);
        PaymentEvent paymentEvent = paymentService.newOrderEvent(orderEvent);
        assertEquals(paymentEvent.getPaymentStatus(), PaymentStatus.RESERVED);
    }

    @Test
    public void testNewOrderEvent_failFindUserBalance(){
        UserBalance userBalance = new UserBalance();
        userBalance.setBalance(1);
        when(userBalanceRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        PaymentEvent paymentEvent = paymentService.newOrderEvent(orderEvent);
        assertEquals(paymentEvent.getPaymentStatus(), PaymentStatus.REJECTED);
    }

    @Test
    public void testNewOrderEvent_failSaveUserTransaction(){
        UserBalance userBalance = new UserBalance();
        userBalance.setBalance(1);
        when(userBalanceRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(userBalance));
        when(userTransactionRepository.save(Mockito.any())).thenThrow(new RuntimeException());
        PaymentEvent paymentEvent = paymentService.newOrderEvent(orderEvent);
        assertEquals(paymentEvent.getPaymentStatus(), PaymentStatus.REJECTED);
    }

    @Test
    public void testNewOrderEvent_whenBalanceIsOver(){
        UserBalance userBalance = new UserBalance();
        userBalance.setBalance(0);
        when(userBalanceRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(userBalance));
        PaymentEvent paymentEvent = paymentService.newOrderEvent(orderEvent);
        assertEquals(paymentEvent.getPaymentStatus(), PaymentStatus.REJECTED);
    }

    @Test
    public void testCancelOrderEvent(){
        UserBalance userBalance = new UserBalance();
        userBalance.setBalance(1);
        UserTransaction userTransaction = new UserTransaction();
        when(userBalanceRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(userBalance));
        when(userTransactionRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(userTransaction));
        doNothing().when(userTransactionRepository).delete(Mockito.any());
        assertAll(() -> paymentService.cancelOrderEvent(orderEvent));
    }

    @Test
    public void testCancelOrderEvent_whenNotFoundUserTransaction(){
        when(userTransactionRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        assertAll(() -> paymentService.cancelOrderEvent(orderEvent));
    }


}
