package org.dbahrim.forum.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dbahrim.forum.AuthenticationTestUtils;
import org.dbahrim.forum.data.CommentRepository;
import org.dbahrim.forum.data.PostRepository;
import org.dbahrim.forum.data.ReportRepository;
import org.dbahrim.forum.models.Comment;
import org.dbahrim.forum.models.Report;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

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
        performReportAsUser("/api/reports/comment/1").andExpect(status().isOk());
    }

    @Test
    public void reportPostAsUserWorks() throws Exception
    {
        performReportAsUser("/api/reports/post/1").andExpect(status().isOk());
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

    @Test
    public void resolveAsAnonymous() throws Exception
    {
        mvc.perform(MockMvcRequestBuilders
                        .patch("/api/reports/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void resolveAsUser() throws Exception
    {
        mvc.perform(MockMvcRequestBuilders
                        .patch("/api/reports/1")
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void resolveAsAdminNoBody() throws Exception
    {
        Long reportId = null;
        for (Report report : reportRepository.findAll()) {
            reportId = report.id;
            break;
        }
        if (reportId == null) {
            reportCommentAsAdminWorks();
            reportId = 1L;
        }
        mvc.perform(MockMvcRequestBuilders
                        .patch("/api/reports/" + reportId)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void resolveAsAdminNoValidReport() throws Exception
    {
        Report.ReportResolution resolution = new Report.ReportResolution("Test test test test", Report.Resolution.NOT_VALID);
        mvc.perform(MockMvcRequestBuilders
                        .patch("/api/reports/999")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(resolution))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void resolveWithDeleteAsAdminComment() throws Exception
    {
        Long reportId = 1L;
        reportCommentAsAdminWorks();
        Report report = reportRepository.findById(reportId).orElseThrow(ErrorController.NotFoundException::new);
        Report.ReportResolution resolution = new Report.ReportResolution("Test test test test", Report.Resolution.DELETED);
        mvc.perform(MockMvcRequestBuilders
                        .patch("/api/reports/" + reportId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(resolution))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        if (report.post != null) {
            assert postRepository.findById(report.post.id).isEmpty();
        } else {
            assert commentRepository.findById(report.comment.getId()).isEmpty();
        }
    }

    @Test
    public void resolveWithDeleteAsAdminPost() throws Exception
    {
        Long reportId = 1L;
        reportPostAsAdminWorks();
        Report report = reportRepository.findById(reportId).orElseThrow(ErrorController.NotFoundException::new);
        Report.ReportResolution resolution = new Report.ReportResolution("Test test test test", Report.Resolution.DELETED);
        mvc.perform(MockMvcRequestBuilders
                        .patch("/api/reports/" + reportId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(resolution))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        if (report.post != null) {
            assert postRepository.findById(report.post.id).isEmpty();
        } else {
            assert commentRepository.findById(report.comment.getId()).isEmpty();
        }
    }

    @Test
    public void resolveWithCleanAsAdminComment() throws Exception
    {
        Report.ReportResolution resolution = new Report.ReportResolution("Test test test test", Report.Resolution.CLEANED);
        long initialSize = commentRepository.findByContent(resolution.message).size();
        Long reportId = 1L;
        reportCommentAsAdminWorks();
        Report report = reportRepository.findById(reportId).orElseThrow(ErrorController.NotFoundException::new);
        mvc.perform(MockMvcRequestBuilders
                        .patch("/api/reports/" + reportId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(resolution))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        assert initialSize + 1 == commentRepository.findByContent(resolution.message).size();
    }

    @Test
    public void resolveWithCleanAsAdminPost() throws Exception
    {
        Report.ReportResolution resolution = new Report.ReportResolution("Test test test test", Report.Resolution.CLEANED);
        long initialSize = postRepository.findByContent(resolution.message).size();
        Long reportId = 1L;
        reportPostAsAdminWorks();
        Report report = reportRepository.findById(reportId).orElseThrow(ErrorController.NotFoundException::new);
        mvc.perform(MockMvcRequestBuilders
                        .patch("/api/reports/" + reportId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(resolution))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        assert initialSize + 1 == postRepository.findByContent(resolution.message).size();
    }

    @Test
    public void resolveWithNotValidAsAdminComment() throws Exception
    {
        Report.ReportResolution resolution = new Report.ReportResolution("Test test test test", Report.Resolution.NOT_VALID);
        reportCommentAsAdminWorks();
        mvc.perform(MockMvcRequestBuilders
                        .patch("/api/reports/" + 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(resolution))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        assert 0 == commentRepository.findByContent(resolution.message).size();
        assert 1 == reportRepository.findByMessage(resolution.message).size();
    }

    @Test
    public void resolveWithNotValidAsAdminPost() throws Exception
    {
        Report.ReportResolution resolution = new Report.ReportResolution("Test test test test", Report.Resolution.NOT_VALID);
        reportPostAsAdminWorks();
        mvc.perform(MockMvcRequestBuilders
                        .patch("/api/reports/" + 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(resolution))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        assert 0 == postRepository.findByContent(resolution.message).size();
        assert 1 == reportRepository.findByMessage(resolution.message).size();
    }
}
