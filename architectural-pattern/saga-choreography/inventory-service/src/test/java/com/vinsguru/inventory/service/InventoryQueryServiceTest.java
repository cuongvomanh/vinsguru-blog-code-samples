package com.vinsguru.inventory.service;

import com.vinsguru.inventory.entity.OrderInventory;
import com.vinsguru.inventory.repository.OrderInventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InventoryQueryServiceTest {

    @Mock
    private OrderInventoryRepository orderInventoryRepository;

    @InjectMocks
    private InventoryQueryService inventoryQueryService;

    @Test
    public void test_findByProductId(){
        when(orderInventoryRepository.findByProductId(Mockito.anyInt())).thenReturn(Optional.ofNullable(OrderInventory.of(1, 2)));
        assertEquals(inventoryQueryService.findByProductId(1).getAvailableInventory(), 2);
    }

    @Test
    public void test_findByProductId_ProductNotFound(){
        when(orderInventoryRepository.findByProductId(Mockito.anyInt())).thenReturn(Optional.empty());
        assertThrows(BadRequestCustomException.class, () -> inventoryQueryService.findByProductId(1).getProductId());
    }

    @Test
    public void test_findByProductId_AvailbleInventoryEqual0(){
        when(orderInventoryRepository.findByProductId(Mockito.anyInt())).thenReturn(Optional.ofNullable(OrderInventory.of(1, 0)));
        assertEquals(inventoryQueryService.findByProductId(1).getAvailableInventory(), 0);
    }

    @Test
    public void test_findByProductId_RuntimeException(){
        when(orderInventoryRepository.findByProductId(Mockito.anyInt())).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> inventoryQueryService.findByProductId(1).getProductId());
    }
}
