package com.vinsguru.order.service;

import com.vinsguru.dto.InventoryDto;
import com.vinsguru.dto.OrderRequestDto;
import com.vinsguru.dto.PaymentDto;
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

    @Autowired
    private PaymentService paymentService;

    private static Logger LOGGER = LoggerFactory.getLogger(OrderCommandService.class);

    @Transactional
    public PurchaseOrder createOrder(OrderRequestDto orderRequestDTO) {
        PurchaseOrder purchaseOrder = this.dtoToEntity(orderRequestDTO);
        validateInventory(purchaseOrder);
        validatePayment(purchaseOrder);
        purchaseOrder = this.purchaseOrderRepository.save(purchaseOrder);
        this.publisher.raiseOrderEvent(purchaseOrder, OrderStatus.ORDER_CREATED);
        return purchaseOrder;
    }

    private void validateInventory(PurchaseOrder purchaseOrder) {
        try {
            InventoryDto inventoryDto = inventoryService.findByProductId(purchaseOrder.getProductId());
            if (inventoryDto == null || inventoryDto.getAvailableInventory() <= 0) {
                throw new BadRequestCustomException(Constant.PRODUCT_OUT_OF_INVENTORY);
            }
        } catch (BadRequestCustomException badRequestCustomException) {
            LOGGER.error("Validate get BadRequest!");
            throw badRequestCustomException;
        } catch (Exception exception) {
            LOGGER.error("Validate order have Exception!");
        }
    }

    private void validatePayment(PurchaseOrder purchaseOrder) {
        try {
            PaymentDto paymentDto = paymentService.findByUserId(purchaseOrder.getUserId());
            if (paymentDto == null || purchaseOrder.getPrice() == null || paymentDto.getBalance() == null || paymentDto.getBalance() <= purchaseOrder.getPrice()) {
                throw new BadRequestCustomException(Constant.USER_NOT_FOUND_OR_USER_OUT_OF_PAYMENT);
            }
        } catch (BadRequestCustomException badRequestCustomException) {
            LOGGER.error("Validate get BadRequest!");
            throw badRequestCustomException;
        } catch (Exception exception) {
            LOGGER.error("Validate order have Exception!");
        }
    }

    private PurchaseOrder dtoToEntity(final OrderRequestDto dto) {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(dto.getOrderId());
        purchaseOrder.setProductId(dto.getProductId());
        purchaseOrder.setUserId(dto.getUserId());
        purchaseOrder.setOrderStatus(OrderStatus.ORDER_CREATED);
        Integer price = productPriceMap.get(purchaseOrder.getProductId());
        if (price == null) {
            throw new BadRequestCustomException(Constant.PRODUCT_NOT_FOUND);
        }
        purchaseOrder.setPrice(price);
        return purchaseOrder;
    }

}
