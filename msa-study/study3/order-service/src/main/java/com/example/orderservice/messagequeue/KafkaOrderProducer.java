package com.example.orderservice.messagequeue;

import com.example.orderservice.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaOrderProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    List<Field> fields = Arrays.asList(
            Field.builder().type("string").optional(true).field("order_id").build(),
            Field.builder().type("string").optional(true).field("user_id").build(),
            Field.builder().type("string").optional(true).field("product_id").build(),
            Field.builder().type("int32").optional(true).field("qty").build(),
            Field.builder().type("int32").optional(true).field("unit_price").build(),
            Field.builder().type("int32").optional(true).field("total_price").build()
    );

    Schema schema = Schema.builder()
            .type("struct")
            .fields(fields)
            .optional(false)
            .name("orders")
            .build();

    public OrderDto orderSend(String topic, OrderDto orderDto){

        Payload payload = Payload.builder()
                .order_id(orderDto.getOrderId())
                .user_id(orderDto.getUserId())
                .product_id(orderDto.getProductId())
                .qty(orderDto.getQty())
                .unit_price(orderDto.getUnitPrice())
                .total_price(orderDto.getTotalPrice())
                .build();

        KafkaOrderDto kafkaOrderDto = KafkaOrderDto.builder()
                .schema(schema)
                .payload(payload)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        //json format으로 변경
        String json = "";
        try {
            json = mapper.writeValueAsString(kafkaOrderDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //kafka 메세지 전송
        kafkaTemplate.send(topic, json);
        log.info("Kafka Producer send data from the order service = {}",kafkaOrderDto);

        return orderDto;
    }
}
