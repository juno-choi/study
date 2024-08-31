package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.messagequeue.KafkaOrderProducer;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order-service")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final Environment env;
    private final OrderService orderService;
    private final KafkaProducer kafkaProducer;  //kafka producer 주입
    private final KafkaOrderProducer kafkaOrderProducer;    //주문 전송 producer 주입

    @GetMapping("/health_check")
    public String status(){
        return String.format("It's Working in User Service on Port %s",env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId, @RequestBody RequestOrder requestOrder){
        log.info("Before Add Order Data");

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        //기존의 jpa 로직
        OrderDto orderDto = mapper.map(requestOrder, OrderDto.class);
        orderDto.setUserId(userId);
        OrderDto createOrder = orderService.createOrder(orderDto);
        ResponseOrder responseOrder = mapper.map(createOrder, ResponseOrder.class);
//        OrderDto orderDto = mapper.map(requestOrder, OrderDto.class);
//        orderDto.setUserId(userId);
        //kafka 주문 로직 추가
//        orderDto.setOrderId(UUID.randomUUID().toString());
//        orderDto.setTotalPrice(requestOrder.getQty() * requestOrder.getUnitPrice());

        //kafka 메세지 전송
//        kafkaProducer.orderSend("example-catalog-topic", orderDto);
//        kafkaOrderProducer.orderSend("orders", orderDto);

//        ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class);

        log.info("After Added Order Data");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId){
        log.info("Before Retrieve Order Data");
        Iterable<OrderEntity> orderList = orderService.getOrderByUserId(userId);
        List<ResponseOrder> result = new ArrayList<>();

        orderList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });

        log.info("After Receive Order Data");
        return ResponseEntity.ok().body(result);
    }
}
