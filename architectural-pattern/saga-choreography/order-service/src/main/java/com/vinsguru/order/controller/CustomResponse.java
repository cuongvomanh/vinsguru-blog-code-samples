package com.vinsguru.order.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class CustomResponse<T> {
    private String errorCode;
    private T dto;
}
