package com.vinsguru.inventory.config;

import com.vinsguru.dto.PurchaseOrderDto;
import com.vinsguru.events.inventory.InventoryEvent;
import com.vinsguru.events.order.OrderEvent;
import com.vinsguru.events.order.OrderStatus;
import com.vinsguru.inventory.entity.OrderInventory;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InventoryConfigTest {

    @InjectMocks
    private InventoryConfig inventoryConfig;

    private static OrderEvent orderEvent;
    private static OrderStatus orderStatus;
    private static PurchaseOrderDto purchaseOrderDto;
    private static Logger LOGGER = LoggerFactory.getLogger(InventoryConfigTest.class);

    @Test
    public void testProcessInventory(){
        orderEvent = new OrderEvent();
        orderStatus = OrderStatus.ORDER_CREATED;
        orderEvent.setOrderStatus(orderStatus);
        purchaseOrderDto = new PurchaseOrderDto(null,1, 1,1);
        orderEvent.setPurchaseOrder(purchaseOrderDto);
        Mono<InventoryEvent> inventoryEventMono = inventoryConfig.processInventory(orderEvent);

//        Map<String, Object> consumerProps = new HashMap<>();
//        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
////        consumerProps.put("bootstrap.servers", kafkaContainer.getBootstrapServers());
//        consumerProps.put("auto.offset.reset", "earliest");
//        consumerProps.put("group.id", "group-produce");
//
//        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(consumerProps);
//        consumer.subscribe(Collections.singletonList("topic-produce"));
//
//        Consumer<InventoryEvent> consumer1 = new Consumer<InventoryEvent>() {
//            @Override
//            public void accept(InventoryEvent inventoryEvent) {
//                LOGGER.info("Accept inventory!");
//            }
//        };
//        consumer.subscribe(Collections.singletonList("topic-produce"));
//        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
//        consumer.
//        inventoryEventMono.doOnEach()
        assertNotNull(inventoryEventMono);
    }
}
