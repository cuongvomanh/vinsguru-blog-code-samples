package com.vinsguru.order.service;

import com.vinsguru.dto.InventoryDto;
import com.vinsguru.dto.OrderRequestDto;
import com.vinsguru.events.order.OrderStatus;
import com.vinsguru.order.config.Constant;
import com.vinsguru.order.entity.PurchaseOrder;
import com.vinsguru.order.repository.PurchaseOrderRepository;
import com.vinsguru.order.service.error.BadRequestCustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@Service
public class OrderCommandService {

    @Autowired
    private Map<Integer, Integer> productPriceMap;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private OrderStatusPublisher publisher;

    @Autowired
    private InventoryService inventoryService;

    private static Logger LOGGER = LoggerFactory.getLogger(OrderCommandService.class);

    @Transactional
    public PurchaseOrder createOrder(OrderRequestDto orderRequestDTO){
        try {
            InventoryDto inventoryDto = inventoryService.findByProductId(orderRequestDTO.getProductId());
            if (inventoryDto == null || inventoryDto.getAvailableInventory() <= 0){
                throw new BadRequestCustomException(Constant.PRODUCT_NOT_FOUND_OR_PRODUCT_OUT_OF_INVENTORY);
            }
        } catch (HttpClientErrorException.BadRequest badRequest){
            throw new BadRequestCustomException(Constant.PRODUCT_NOT_FOUND_OR_PRODUCT_OUT_OF_INVENTORY);
        } catch (Exception exception){
            LOGGER.error("Get inventory have Exception!");
        }
        PurchaseOrder purchaseOrder = this.purchaseOrderRepository.save(this.dtoToEntity(orderRequestDTO));
        this.publisher.raiseOrderEvent(purchaseOrder, OrderStatus.ORDER_CREATED);
        return purchaseOrder;
    }

    private PurchaseOrder dtoToEntity(final OrderRequestDto dto){
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(dto.getOrderId());
        purchaseOrder.setProductId(dto.getProductId());
        purchaseOrder.setUserId(dto.getUserId());
        purchaseOrder.setOrderStatus(OrderStatus.ORDER_CREATED);
        Integer price = productPriceMap.get(purchaseOrder.getProductId());
        if (price == null){
            throw new RuntimeException(Constant.PRICE_IS_UNKNOW);
        }
        purchaseOrder.setPrice(price);
        return purchaseOrder;
    }

}
