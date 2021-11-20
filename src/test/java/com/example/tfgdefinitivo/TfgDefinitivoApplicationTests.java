package com.example.tfgdefinitivo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TfgDefinitivoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private MockMvc mvc;

//    @Test
//    public void getHello() throws Exception {
//        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
//                .andExpect((ResultMatcher) status().isOk())
//                .andExpect((ResultMatcher) content().string(equalTo("Greetings from Spring Boot!")));
//    }

}
