package com.test.code.acceptance.service;

import org.springframework.stereotype.Service;

@Service
public class AcceptanceServiceImpl implements AcceptanceService {
    @Override
    public String hello(String path) {
        return String.format("hello %s", path);
    }
}
