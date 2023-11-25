package org.dbahrim.forum.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dbahrim.forum.AuthenticationTestUtils;
import org.dbahrim.forum.data.ReportRepository;
import org.dbahrim.forum.models.Report;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class TestReportsController {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AuthenticationTestUtils testUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReportRepository reportRepository;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    public void exit() {
        reportRepository.findAll().forEach(e -> {
            try {
                System.out.println(objectMapper.writeValueAsString(e));
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        });

    }

    @Test
    public void getAllReportsNotLoggedIn() throws Exception
    {
        mvc.perform(MockMvcRequestBuilders
                        .get("/api/reports")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getAllReportsAsUser() throws Exception
    {
        mvc.perform(MockMvcRequestBuilders
                        .get("/api/reports")
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void getAllReportsAsAdmin() throws Exception
    {
        mvc.perform(MockMvcRequestBuilders
                        .get("/api/reports")
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void reportCommentAsAnonymousReturns403() throws Exception
    {
        performReportAsAnonymous("/api/reports/comment/1");
    }

    @Test
    public void reportPostAsAnonymousReturns403() throws Exception
    {
        performReportAsAnonymous("/api/reports/post/1");
    }

    private void performReportAsAnonymous(String url) throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void reportCommentAsUserWorks() throws Exception
    {
        performReportAsUser("/api/reports/comment/3").andExpect(status().isOk());
    }

    @Test
    public void reportPostAsUserWorks() throws Exception
    {
        performReportAsUser("/api/reports/post/2").andExpect(status().isOk());
    }

    private ResultActions performReportAsUser(String url) throws Exception {
        Report.ReportDto body = new Report.ReportDto("Not appropriate");
        return mvc.perform(MockMvcRequestBuilders
                        .post(url)
                        .header(testUtils.authorization, testUtils.user())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void reportCommentAsAdminWorks() throws Exception
    {
        performReportAsAdmin("/api/reports/comment/1");
    }

    @Test
    public void reportPostAsAdminWorks() throws Exception
    {
        performReportAsAdmin("/api/reports/post/1");
    }

    private void performReportAsAdmin(String url) throws Exception {
        Report.ReportDto body = new Report.ReportDto("Not appropriate");
        mvc.perform(MockMvcRequestBuilders
                        .post(url)
                        .header(testUtils.authorization, testUtils.admin())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    public void reportCommentAsUserDoesNotWorkWhenNoCommentFound() throws Exception
    {
        performReportAsUser("/api/reports/comment/100").andExpect(status().isNotFound());
    }

    @Test
    public void reportPostAsUserDoesNotWorkWhenNoCommentFound() throws Exception
    {
        performReportAsUser("/api/reports/post/100").andExpect(status().isNotFound());
    }

    @Test
    public void reportCommentAsUserDoesNotWorkWithEmptyBody() throws Exception
    {
        performReportAsUserWithEmptyBody("/api/reports/comment/1");
    }

    @Test
    public void reportPostAsUserDoesNotWorkWithEmptyBody() throws Exception
    {
        performReportAsUserWithEmptyBody("/api/reports/post/1");
    }

    private void performReportAsUserWithEmptyBody(String url) throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post(url)
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
