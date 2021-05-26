package com.vinsguru.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProductPriceConfig {

    // product price map
    @Bean
    public Map<Integer, Integer> productPrice() {
        return new HashMap<Integer, Integer>() {{
            put(1, 100);
            put(2, 200);
            put(3, 300);
        }};
    }
}
