package com.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration  //해당 어노테이션을 통해 spring이 시작될때 context에 자동으로 등록되도록 한다.
public class FilterConfig {
    //@Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder){
        return builder.routes()
                .route(r-> r.path("/first-service/**")
                        .filters(f-> f.addRequestHeader("first-request","first-request-header")
                                    .addResponseHeader("first-response","first-response-header"))
                        .uri("http://127.0.0.1:8001"))  //기존 yml에서 1번 서비스 설정과 동일
                .route(r-> r.path("/second-service/**")
                        .filters(f-> f.addRequestHeader("second-request","second-request-header")
                                    .addResponseHeader("second-response","second-response-header"))
                        .uri("http://127.0.0.1:8002"))  //기존 yml에서 2번 서비스 설정과 동일
                .build();
    }
}
