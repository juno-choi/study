package com.test.code.acceptance.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AcceptanceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void hello_test() throws Exception{
        // given
        String path = "test";

        // when
        ResultActions perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/acceptance/hello")
                        .param("path", path)
        );

        // then
        perform.andExpect(status().isOk());
        perform.andExpect(content().string(String.format("hello %s", path)));
    }
}