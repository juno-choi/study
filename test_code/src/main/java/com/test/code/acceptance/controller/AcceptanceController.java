package com.test.code.acceptance.controller;

import com.test.code.acceptance.service.AcceptanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/acceptance")
@RequiredArgsConstructor
public class AcceptanceController {
    private final AcceptanceService acceptanceService;

    @GetMapping("/hello")
    public String hello(@RequestParam String path) {
        return acceptanceService.hello(path);
    }
}
