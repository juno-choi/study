package com.example.service2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/second-service")
@Slf4j
public class HelloController {
    @GetMapping("hello")
    public String hello(){
        return "hello second service";
    }

    @GetMapping("/message")
    public String message(@RequestHeader("second-request") String header){
        log.info("header = {}",header);
        return "header = "+header;
    }

    @GetMapping("/check")
    public String check(){
        return "Hi, there. This is a message from second Service";
    }
}
