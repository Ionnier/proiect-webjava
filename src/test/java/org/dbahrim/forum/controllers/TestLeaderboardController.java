package org.dbahrim.forum.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dbahrim.forum.AuthenticationTestUtils;
import org.dbahrim.forum.models.Post;
import org.dbahrim.forum.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestLeaderboardController {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void getTopUsers() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/api/top/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        List<User> users = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<User>>() {});
    }

    @Test
    public void getTopPosts() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/api/top/posts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        List<Post> posts = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Post>>() {});
    }

    @Test
    public void getTopPostsWrongWhen() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/api/top/posts?when=asd")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getTopPostsTodayRandomCase() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/api/top/posts?when=tOdAy")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        List<Post> posts = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Post>>() {});
        assert (posts.size() == 3);
    }

    @Test
    public void getTopPostsWeek() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/api/top/posts?when=week")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        List<Post> posts = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Post>>() {});
        assert (posts.size() == 4);
    }

    @Test
    public void getTopPostMonth() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/api/top/posts?when=month")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        List<Post> posts = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Post>>() {});
        assert (posts.size() == 5);
    }
}
