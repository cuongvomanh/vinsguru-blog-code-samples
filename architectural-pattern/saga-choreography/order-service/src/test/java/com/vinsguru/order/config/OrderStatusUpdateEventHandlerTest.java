package com.vinsguru.order.config;

import com.vinsguru.dto.OrderRequestDto;
import com.vinsguru.dto.PaymentDto;
import com.vinsguru.events.inventory.InventoryStatus;
import com.vinsguru.events.order.OrderStatus;
import com.vinsguru.events.payment.PaymentEvent;
import com.vinsguru.events.payment.PaymentStatus;
import com.vinsguru.order.entity.PurchaseOrder;
import com.vinsguru.order.repository.PurchaseOrderRepository;
import com.vinsguru.order.service.OrderStatusPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Consumer;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderStatusUpdateEventHandlerTest {

    @Mock
    private OrderStatusPublisher orderStatusPublisher;

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @InjectMocks
    private OrderStatusUpdateEventHandler orderStatusUpdateEventHandler;

    private static OrderRequestDto orderRequestDto = new OrderRequestDto();
    private static PurchaseOrder purchaseOrder = new PurchaseOrder();

    @BeforeEach
    private void init(){
        orderRequestDto.setProductId(1);
        orderRequestDto.setUserId(1);
        purchaseOrder.setProductId(1);
        purchaseOrder.setUserId(1);
    }

    @Test
    public void testUpdateOrder(){
        when(purchaseOrderRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(purchaseOrder));
        PaymentEvent paymentEvent = new PaymentEvent();
        Consumer<PurchaseOrder> consumer = po -> {
            po.setPaymentStatus(paymentEvent.getPaymentStatus());
        };
        orderStatusUpdateEventHandler.updateOrder(null, consumer);
    }

    @Test
    public void testUpdateOrder_whenPurchaseOrderNotFound(){
        when(purchaseOrderRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        PaymentEvent paymentEvent = new PaymentEvent();
        Consumer<PurchaseOrder> consumer = po -> {
            po.setPaymentStatus(paymentEvent.getPaymentStatus());
        };
        orderStatusUpdateEventHandler.updateOrder(null, consumer);
    }

    @Test
    public void testBussinessUpdateOrder(){
        purchaseOrder.setInventoryStatus(InventoryStatus.RESERVED);
        purchaseOrder.setPaymentStatus(PaymentStatus.RESERVED);
        orderStatusUpdateEventHandler.updateOrder(purchaseOrder);
    }

    @Test
    public void testBussinessUpdateOrder_InventoryStatusReserved_PaymentStatusReject(){
        purchaseOrder.setInventoryStatus(InventoryStatus.RESERVED);
        purchaseOrder.setPaymentStatus(PaymentStatus.REJECTED);
        doNothing().when(orderStatusPublisher).raiseOrderEvent(Mockito.any(), Mockito.any());
        orderStatusUpdateEventHandler.updateOrder(purchaseOrder);
    }
}
