package org.dbahrim.forum.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dbahrim.forum.AuthenticationTestUtils;
import org.dbahrim.forum.configuration.InitializationConfig;
import org.dbahrim.forum.configuration.security.UserRequest;
import org.dbahrim.forum.data.CategoryRepository;
import org.dbahrim.forum.data.UserRepository;
import org.dbahrim.forum.models.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class TestAuthController {

    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Category.CategoryRequestBodyPutPatch validPutPatch = new Category.CategoryRequestBodyPutPatch(1L, "Category 7", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setup(WebApplicationContext context) throws ErrorController.NotFoundException {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testValidSignUp() throws Exception {
        long initialSize = userRepository.findAll().size();
        mvc.perform(MockMvcRequestBuilders
                        .post("/api/signup")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(new UserRequest("hello@cti.ro", "hello!hello")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assert userRepository.findAll().size() == initialSize + 1;
    }

    @Test
    public void testValidLogin() throws Exception {
        testValidSignUp();
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .post("/api/login")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(new UserRequest("hello@cti.ro", "hello!hello")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        assert mvcResult.getResponse().getContentAsString().length()> 20;
    }

}
