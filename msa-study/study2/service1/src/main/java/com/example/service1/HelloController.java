package com.example.service1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/first-service")
@Slf4j
@RequiredArgsConstructor
public class HelloController {

    private final Environment env;

    @GetMapping("hello")
    public String hello(){
        return "hello first service";
    }

    @GetMapping("/message")
    public String message(@RequestHeader("first-request") String header){
        log.info("header = {}",header);
        return "header = "+header;
    }

    @GetMapping("/check")
    public String check(HttpServletRequest request){
        log.info("server port = {}",request.getServerPort());
        return String.format("Hi, there. This is a message from first Service Port = %s", env.getProperty("local.server.port"));
    }
}
