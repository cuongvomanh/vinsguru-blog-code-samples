package com.vinsguru.inventory.controller;


import com.vinsguru.dto.InventoryDto;
import com.vinsguru.inventory.service.BadRequestCustomException;
import com.vinsguru.inventory.service.InventoryQueryService;
import com.vinsguru.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    @Autowired
    private InventoryQueryService inventoryQueryService;

    @GetMapping("/findByProductId")
    public ResponseEntity<InventoryDto> findByProductId(@RequestParam Integer productId){
        try {
            return ResponseEntity.ok(inventoryQueryService.findByProductId(productId));
        } catch (BadRequestCustomException badRequestCustomException){
            return ResponseEntity.badRequest().build();
        } catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
