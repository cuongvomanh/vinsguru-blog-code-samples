package com.vinsguru.order.controller;

import com.vinsguru.dto.OrderRequestDto;
import com.vinsguru.order.entity.PurchaseOrder;
import com.vinsguru.order.service.OrderCommandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderCommandService orderCommandService;

    @InjectMocks
    private OrderController orderController;

    @Test
    public void testCreateOrder(){
        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setProductId(1);
        orderRequestDto.setUserId(1);
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        when(orderCommandService.createOrder(Mockito.any())).thenReturn(purchaseOrder);
        PurchaseOrder purchaseOrder1 = orderController.createOrder(orderRequestDto);
        assertEquals(purchaseOrder, purchaseOrder1);
    }

}
