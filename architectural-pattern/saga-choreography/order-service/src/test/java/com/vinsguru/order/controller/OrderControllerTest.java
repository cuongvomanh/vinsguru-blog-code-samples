package com.vinsguru.order.controller;

import com.vinsguru.dto.OrderRequestDto;
import com.vinsguru.order.config.Constant;
import com.vinsguru.order.entity.PurchaseOrder;
import com.vinsguru.order.service.OrderCommandService;
import com.vinsguru.order.service.error.BadRequestCustomException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpServerErrorException;

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
        PurchaseOrder purchaseOrder1 = orderController.createOrder(orderRequestDto).getBody().getDto();
        assertEquals(purchaseOrder, purchaseOrder1);
    }

    @Test
    public void testCreateOrder_ProductNotFoundOrProductOutOfInventory(){
        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setProductId(1);
        orderRequestDto.setUserId(1);
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        when(orderCommandService.createOrder(Mockito.any())).thenThrow(new BadRequestCustomException(Constant.PRODUCT_NOT_FOUND_OR_PRODUCT_OUT_OF_INVENTORY));
        assertEquals(orderController.createOrder(orderRequestDto).getBody().getErrorCode(), Constant.PRODUCT_NOT_FOUND_OR_PRODUCT_OUT_OF_INVENTORY);
    }
}
