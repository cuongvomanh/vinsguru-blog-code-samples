package com.vinsguru.order.controller;

import com.vinsguru.dto.OrderRequestDto;
import com.vinsguru.order.config.Constant;
import com.vinsguru.order.entity.PurchaseOrder;
import com.vinsguru.order.service.OrderCommandService;
import com.vinsguru.order.service.OrderQueryService;
import com.vinsguru.order.service.error.BadRequestCustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderCommandService commandService;

    @Autowired
    private OrderQueryService queryService;

    @PostMapping("/create")
    public ResponseEntity<CustomResponse<PurchaseOrder>> createOrder(@RequestBody OrderRequestDto requestDTO){
        requestDTO.setOrderId(UUID.randomUUID());
        try {
            return ResponseEntity.ok().body(CustomResponse.of(null, this.commandService.createOrder(requestDTO)));
        } catch (BadRequestCustomException exception){
            return ResponseEntity.badRequest().body(CustomResponse.of(exception.getMessage(), null));
        }
    }

    @GetMapping("/all")
    public List<PurchaseOrder> getOrders(){
        return this.queryService.getAll();
    }

}
