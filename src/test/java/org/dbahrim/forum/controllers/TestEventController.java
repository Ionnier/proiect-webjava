package org.dbahrim.forum.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.dbahrim.forum.AuthenticationTestUtils;
import org.dbahrim.forum.models.Category;
import org.dbahrim.forum.models.Event;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestEventController {
    private MockMvc mvc;

    @Autowired
    private AuthenticationTestUtils testUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup(WebApplicationContext context) {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private final Event validPostRequest = new Event(null, "A valid event title", Instant.now().toEpochMilli(), null);

    @Test
    public void everyMethodToEventsReturns403AsAnonymous() throws Exception {
        for (HttpMethod method: List.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.PATCH, HttpMethod.DELETE, HttpMethod.PUT)) {
            mvc.perform(MockMvcRequestBuilders.request(method, "/api/events")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }

    }

    @Test
    public void getAllReturnsAsUser() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/api/events")
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(result -> {
                    List<Event> events = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assert events.size() == 1;
                });
    }

    @Test
    public void postAsUserWorks() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/api/events")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(validPostRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(result -> {
                    Event event = objectMapper.readValue(result.getResponse().getContentAsString(), Event.class);
                });
    }

    @Test
    public void postIgnoresNotInteractiveFields() throws Exception {
        Event testData = validPostRequest.toBuilder().id(5L).createdBy(testUtils.getAdmin()).build();
        mvc.perform(MockMvcRequestBuilders
                        .post("/api/events")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(testData))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(result -> {
                    Event event = objectMapper.readValue(result.getResponse().getContentAsString(), Event.class);
                    assert !event.getCreatedBy().equals(testData.getCreatedBy());
                    assert !event.getId().equals(testData.getId());
                });
    }

    @Test
    public void postInSameDayWithSameNameFails() throws Exception {
        postAsUserWorks();
        mvc.perform(MockMvcRequestBuilders
                        .post("/api/events")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(validPostRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(result -> {
                    ErrorController.MessageClass error = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorController.MessageClass.class);
                    Assertions.assertEquals(error.message, "Can't create event with same name in the same day");
                });
    }

    @Test
    public void postWithMoogleHasKupo() throws Exception {
        postAsUserWorks();
        mvc.perform(MockMvcRequestBuilders
                        .post("/api/events")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(validPostRequest.toBuilder().title("asdasdmoogle").build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(result -> {
                    Event event = objectMapper.readValue(result.getResponse().getContentAsString(), Event.class);
                    String value = event.title.substring(event.title.length()-4);
                    Assertions.assertEquals("kupo", value);
                });
    }

    @Test
    public void deletePost() throws Exception {
        postAsUserWorks();
        mvc.perform(MockMvcRequestBuilders
                        .delete("/api/events/1")
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteNoPost() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .delete("/api/events/1")
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteAnotherId() throws Exception {
        postAsUserWorks();
        mvc.perform(MockMvcRequestBuilders
                        .delete("/api/events/1")
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }



}
