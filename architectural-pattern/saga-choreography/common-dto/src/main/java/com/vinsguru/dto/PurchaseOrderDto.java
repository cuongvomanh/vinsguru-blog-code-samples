package com.vinsguru.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
//@AllArgsConstructor(staticName = "of")
public class PurchaseOrderDto {

    private UUID orderId;
    private Integer productId;
    private Integer price;
    private Integer userId;

    public PurchaseOrderDto(UUID orderId, Integer productId, Integer price, Integer userId) {
        this.orderId = orderId;
        this.productId = productId;
        this.price = price;
        this.userId = userId;
    }
}
