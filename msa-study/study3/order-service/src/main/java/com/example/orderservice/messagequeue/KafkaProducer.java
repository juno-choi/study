package com.example.orderservice.messagequeue;

import com.example.orderservice.dto.OrderDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderDto orderSend(String topic, OrderDto orderDto){
        ObjectMapper mapper = new ObjectMapper();
        //json format으로 변경
        String json = "";
        try {
            json = mapper.writeValueAsString(orderDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //kafka 메세지 전송
        kafkaTemplate.send(topic, json);
        log.info("Kafka Producer send data from the order service = {}",orderDto);

        return orderDto;
    }
}
