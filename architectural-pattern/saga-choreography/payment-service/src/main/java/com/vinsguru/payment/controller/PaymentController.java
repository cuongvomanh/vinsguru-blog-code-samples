package com.vinsguru.payment.controller;


import com.vinsguru.dto.InventoryDto;
import com.vinsguru.dto.PaymentDto;
import com.vinsguru.payment.service.BadRequestCustomException;
import com.vinsguru.payment.service.PaymentQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentQueryService paymentQueryService;

    @GetMapping("/findByUserId")
    public ResponseEntity<PaymentDto> findByProductId(@RequestParam Integer userId){
        try {
            return ResponseEntity.ok(paymentQueryService.findByUserId(userId));
        } catch (BadRequestCustomException badRequestCustomException){
            return ResponseEntity.badRequest().build();
        } catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
