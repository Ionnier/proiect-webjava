package org.dbahrim.forum.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dbahrim.forum.AuthenticationTestUtils;
import org.dbahrim.forum.data.CommentRepository;
import org.dbahrim.forum.data.PostRepository;
import org.dbahrim.forum.data.ReportRepository;
import org.dbahrim.forum.models.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestPostController {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AuthenticationTestUtils testUtils;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }


    @Test
    public void getAllAsAnonymousWorks() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/api/posts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllAsUserWorks() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/api/posts")
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllAsAdminWorks() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/api/posts")
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getOneAsAnonymousWorks() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/api/posts/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getOneAsUserWorks() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/api/posts/1")
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getOneAsAdminWorks() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/api/posts/1")
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getMissingOneReturnsNotFound() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/api/posts/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
