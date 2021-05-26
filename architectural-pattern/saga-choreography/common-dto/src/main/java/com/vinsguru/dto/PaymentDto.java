package com.vinsguru.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
//@AllArgsConstructor(staticName = "of")
public class PaymentDto {

    private UUID orderId;
    private Integer userId;
    private Integer amount;

    public PaymentDto(UUID orderId, Integer userId, Integer amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
    }
}
