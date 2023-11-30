package org.dbahrim.forum.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dbahrim.forum.AuthenticationTestUtils;
import org.dbahrim.forum.configuration.InitializationConfig;
import org.dbahrim.forum.data.CommentRepository;
import org.dbahrim.forum.data.PostRepository;
import org.dbahrim.forum.models.Category;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestVoteController {

    private final String basePath = "/api/vote";
    private final String commentPath = "/comment";
    private final String postPath = "/post";
    private final String upPath = "/up";
    private final String downPath = "/down";
    private final String cancelPath = "/cancel";

    private MockMvc mvc;

    private static final Category.CategoryRequestBodyPutPatch validPutPatch = new Category.CategoryRequestBodyPutPatch(1L, "Category 7", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AuthenticationTestUtils testUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private InitializationConfig initializationConfig;

    @BeforeEach
    public void setup() throws ErrorController.NotFoundException {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void upvoteAsAnonymousDoesNotWork() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .post(basePath + postPath + upPath + "/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    public void downvoteAsAnonymousDoesNotWork() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .post(basePath + postPath + downPath + "/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    public void upvotePostAsUserWorks() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .post(basePath + postPath + upPath + "/1")
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assert (long) postRepository.findById(1L).orElseThrow(ErrorController.NotFoundException::new).upvotedBy.size() == 1;
    }

    @Test
    public void downvotePostAsUserWorks() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .post(basePath + postPath + downPath + "/1")
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assert (long) postRepository.findById(1L).orElseThrow(ErrorController.NotFoundException::new).dislikedBy.size() == 1;
    }

    @Test
    public void upvoteCommentAsUserWorks() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .post(basePath + commentPath + upPath + "/1")
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assert (long) commentRepository.findById(1L).orElseThrow(ErrorController.NotFoundException::new).upvotedBy.size() == 1;
    }

    @Test
    public void downVoteCommentAsUserWorks() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .post(basePath + commentPath + downPath + "/1")
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assert (long) commentRepository.findById(1L).orElseThrow(ErrorController.NotFoundException::new).dislikedBy.size() == 1;
    }

    @Test
    public void upvotePostAsAdminWorks() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .post(basePath + postPath + upPath + "/1")
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assert (long) postRepository.findById(1L).orElseThrow(ErrorController.NotFoundException::new).upvotedBy.size() == 1;
    }

    @Test
    public void downvotePostAsAdminWorks() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .post(basePath + postPath + downPath + "/1")
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assert (long) postRepository.findById(1L).orElseThrow(ErrorController.NotFoundException::new).dislikedBy.size() == 1;
    }

    @Test
    public void upvoteCommentAsAdminWorks() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .post(basePath + commentPath + upPath + "/1")
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assert (long) commentRepository.findById(1L).orElseThrow(ErrorController.NotFoundException::new).upvotedBy.size() == 1;
    }

    @Test
    public void downVoteCommentAsAdminWorks() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .post(basePath + commentPath + downPath + "/1")
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assert (long) commentRepository.findById(1L).orElseThrow(ErrorController.NotFoundException::new).dislikedBy.size() == 1;
    }

    @Test
    public void cancelDownvoteCommentVoteAsAdminWorks() throws Exception {
        downVoteCommentAsAdminWorks();
        assert (long) commentRepository.findById(1L).orElseThrow(ErrorController.NotFoundException::new).dislikedBy.size() == 1;
        mvc.perform(
                MockMvcRequestBuilders
                        .post(basePath + commentPath + cancelPath + "/1")
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assert (long) commentRepository.findById(1L).orElseThrow(ErrorController.NotFoundException::new).dislikedBy.size() == 0;
    }

    @Test
    public void cancelUpVoteCommentVoteAsAdminWorks() throws Exception {
        upvoteCommentAsAdminWorks();
        assert (long) commentRepository.findById(1L).orElseThrow(ErrorController.NotFoundException::new).upvotedBy.size() == 1;
        mvc.perform(
                MockMvcRequestBuilders
                        .post(basePath + commentPath + cancelPath + "/1")
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assert (long) commentRepository.findById(1L).orElseThrow(ErrorController.NotFoundException::new).upvotedBy.size() == 0;
    }

    @Test
    public void dislikingAnUpvotedCommentAsAdmin() throws Exception {
        upvoteCommentAsAdminWorks();
        assert (long) commentRepository.findById(1L).orElseThrow(ErrorController.NotFoundException::new).upvotedBy.size() == 1;
        downVoteCommentAsAdminWorks();
        assert (long) commentRepository.findById(1L).orElseThrow(ErrorController.NotFoundException::new).upvotedBy.size() == 0;
        assert (long) commentRepository.findById(1L).orElseThrow(ErrorController.NotFoundException::new).dislikedBy.size() == 1;
    }

    @Test
    public void upvotingAnDislikedCommentAsAdmin() throws Exception {
        downVoteCommentAsAdminWorks();
        assert (long) commentRepository.findById(1L).orElseThrow(ErrorController.NotFoundException::new).dislikedBy.size() == 1;
        upvoteCommentAsAdminWorks();
        assert (long) commentRepository.findById(1L).orElseThrow(ErrorController.NotFoundException::new).dislikedBy.size() == 0;
        assert (long) commentRepository.findById(1L).orElseThrow(ErrorController.NotFoundException::new).upvotedBy.size() == 1;
    }

}