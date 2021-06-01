package com.vinsguru.order.service;

import com.vinsguru.dto.InventoryDto;
import com.vinsguru.order.config.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class InventoryService {
    private static Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);
    @Value("${service.inventory_service_url}")
    private String INVENTORY_SERVICE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    public InventoryDto findByProductId(Integer productId) {
        try {
            HttpEntity<Void> request = new HttpEntity<Void>(new HttpHeaders());
            InventoryDto inventoryDtoResponseEntity = restTemplate
                    .exchange(INVENTORY_SERVICE_URL + Constant.INVENTORY_SERVICE + Constant.FIND_BY_PRODUCT_ID + "?productId={productId}", HttpMethod.GET, request, InventoryDto.class, productId).getBody();
            return inventoryDtoResponseEntity;
        } catch (HttpClientErrorException.BadRequest ex) {
            LOGGER.error(Constant.PRODUCT_NOT_FOUND_OR_PRODUCT_OUT_OF_INVENTORY);
            throw ex;
        } catch (Exception exception){
            LOGGER.error("ERROR when call /inventory/findByProductId");
            throw exception;
        }
    }
}
