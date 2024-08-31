package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import com.example.userservice.vo.ResponseUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder pwdEncoder;
    //private final RestTemplate restTemplate;
    private final OrderServiceClient orderServiceClient;    //FeignClient의 interface 받기
    private final Environment env;
    private final CircuitBreakerFactory circuitBreakerFactory;
    
    @Override
    public ResponseUser createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(pwdEncoder.encode(userDto.getPwd()));

        UserEntity save = userRepository.save(userEntity);
        ResponseUser responseUser = mapper.map(save, ResponseUser.class);

        return responseUser;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if(userEntity == null) throw new UsernameNotFoundException("user name not found!");
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
//        List<ResponseOrder> orderList = new ArrayList<>();    //이전에 빈 배열을 반환하던 값

        /* Using as RestTemplate */
//        String orderUrl = String.format(env.getProperty("order_service.url"), userId);
//        ResponseEntity<List<ResponseOrder>> orderListResponse =
//                restTemplate.exchange(orderUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<ResponseOrder>>() {
//                });
//        List<ResponseOrder> orderList = orderListResponse.getBody();

        /* Using as FeignClient */
        //List<ResponseOrder> orderList = orderServiceClient.getOrders(userId);

        /* FeignClient exception handling*/
//        List<ResponseOrder> orderList = null;
//        try {
//            orderList = orderServiceClient.getOrders(userId);
//        }catch (FeignException e){
//            log.error(e.getMessage());
//        }

        /* 기존 getOrders() */
        //List<ResponseOrder> orderList = orderServiceClient.getOrders(userId);
        log.info("Before Call Order Service");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitBreaker");
        
        List<ResponseOrder> orderList = circuitBreaker.run(() -> 
            orderServiceClient.getOrders(userId),
            throwable -> new ArrayList<>()  //Error가 발생할 경우 비어있는 리스트를 반환
        );

        log.info("After Call Order Service");
        userDto.setOrders(orderList);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if(userEntity == null) throw new UsernameNotFoundException("user가 존재하지 않습니다.");

        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(), true, true, true, true, new ArrayList<>());
    }

    @Override
    public UserDto getUserDetailsByEmail(String userEmail) {
        UserEntity userEntity = userRepository.findByEmail(userEmail);
        //if(userEntity == null) throw new UsernameNotFoundException("user email 값이 유효하지 않습니다.");
        return new ModelMapper().map(userEntity, UserDto.class);
    }
}
