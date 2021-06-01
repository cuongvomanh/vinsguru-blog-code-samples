package com.vinsguru.order.service;

import com.vinsguru.dto.InventoryDto;
import com.vinsguru.dto.PaymentDto;
import com.vinsguru.order.config.Constant;
import com.vinsguru.order.service.error.BadRequestCustomException;
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
public class PaymentService {
    private static Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);
    @Value("${service.payment_service_url}")
    private String PAYMENT_SERVICE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    public PaymentDto findByUserId(Integer userId) {
        try {
            HttpEntity<Void> request = new HttpEntity<Void>(new HttpHeaders());
            PaymentDto paymentDto = restTemplate
                    .exchange(PAYMENT_SERVICE_URL + Constant.PAYMENT_SERVICE + Constant.FIND_BY_USER_ID + "?userId={userId}", HttpMethod.GET, request, PaymentDto.class, userId).getBody();
            return paymentDto;
        } catch (HttpClientErrorException.BadRequest ex) {
            LOGGER.error(Constant.USER_NOT_FOUND_OR_USER_OUT_OF_PAYMENT);
            throw new BadRequestCustomException(Constant.USER_NOT_FOUND_OR_USER_OUT_OF_PAYMENT);
        } catch (Exception exception){
            LOGGER.error("ERROR when call /payment/findByUserId");
            throw exception;
        }
    }
}
