package com.vinsguru.order.service;

import com.vinsguru.dto.InventoryDto;
import com.vinsguru.dto.OrderRequestDto;
import com.vinsguru.order.config.Constant;
import com.vinsguru.order.entity.PurchaseOrder;
import com.vinsguru.order.repository.PurchaseOrderRepository;
import com.vinsguru.order.service.error.BadRequestCustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpServerErrorException;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderCommandServiceTest {

    @Mock
    private HashMap<Integer, Integer> productPriceMap;

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private OrderStatusPublisher orderStatusPublisher;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private OrderCommandService orderCommandService;

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
    public void testCreateOrder(){
        when(inventoryService.findByProductId(Mockito.any())).thenReturn(InventoryDto.of(null, 1, 2));
        when(purchaseOrderRepository.save(Mockito.any())).thenReturn(purchaseOrder);
        when(productPriceMap.get(Mockito.any())).thenReturn(10);
        doNothing().when(orderStatusPublisher).raiseOrderEvent(Mockito.any(), Mockito.any());
        PurchaseOrder purchaseOrder1 = orderCommandService.createOrder(orderRequestDto);
        assertEquals(purchaseOrder, purchaseOrder1);
    }

    @Test
    public void testCreateOrder_whenSavePurchaseOrderFailed(){
        when(inventoryService.findByProductId(Mockito.any())).thenReturn(InventoryDto.of(null, 1, 2));
        when(productPriceMap.get(Mockito.any())).thenReturn(10);
        when(purchaseOrderRepository.save(Mockito.any())).thenThrow(new RuntimeException());
        assertThrows(Exception.class, () ->orderCommandService.createOrder(orderRequestDto));
    }

    @Test
    public void testCreateOrder_whenProductNotFound(){
        when(productPriceMap.get(Mockito.any())).thenReturn(null);
        assertThrows(BadRequestCustomException.class, () -> orderCommandService.createOrder(orderRequestDto));
    }

    @Test
    public void testCreateOrder_whenRaiseOrderEventFailed(){
        when(inventoryService.findByProductId(Mockito.any())).thenReturn(InventoryDto.of(null, 1, 2));
        when(purchaseOrderRepository.save(Mockito.any())).thenReturn(purchaseOrder);
        when(productPriceMap.get(Mockito.any())).thenReturn(10);
        doThrow(new RuntimeException()).when(orderStatusPublisher).raiseOrderEvent(Mockito.any(), Mockito.any());
        assertThrows(Exception.class, () ->orderCommandService.createOrder(orderRequestDto));
    }

    @Test
    public void testCreateOrder_whenOutOfProductInventory(){
        when(productPriceMap.get(Mockito.any())).thenReturn(10);
        when(inventoryService.findByProductId(Mockito.any())).thenReturn(InventoryDto.of(null, 1, 0));
        assertThrows(BadRequestCustomException.class, () -> orderCommandService.createOrder(orderRequestDto));
    }

    @Test
    public void testCreateOrder_whenErrorGetProductInventory(){
        when(productPriceMap.get(Mockito.any())).thenReturn(10);
        when(inventoryService.findByProductId(Mockito.any())).thenThrow(new NullPointerException());
        assertAll(() -> orderCommandService.createOrder(orderRequestDto));
    }

    @Test
    public void testCreateOrder_whenCustomBadRequestExceptionWhenFindProductInventory(){
        when(productPriceMap.get(Mockito.any())).thenReturn(10);
        when(inventoryService.findByProductId(Mockito.any())).thenThrow(new BadRequestCustomException(Constant.PRODUCT_NOT_FOUND));
        assertThrows(BadRequestCustomException.class ,() -> orderCommandService.createOrder(orderRequestDto));
    }

}
