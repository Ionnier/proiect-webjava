package org.dbahrim.forum.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dbahrim.forum.AuthenticationTestUtils;
import org.dbahrim.forum.configuration.InitializationConfig;
import org.dbahrim.forum.data.CategoryRepository;
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

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestCategoryController {
    private MockMvc mvc;

    private static final Category.CategoryRequestBodyPutPatch validPutPatch = new Category.CategoryRequestBodyPutPatch(1L, "Category 7", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AuthenticationTestUtils testUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

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
    public void getAllAsAnonymousWorks() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .get("/api/categories")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    public void getAllAsUserWorks() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .get("/api/categories")
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    public void getAllAsAdminWorks() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .get("/api/categories")
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    public void getOneWorks() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .get("/api/categories/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    public void getOneMissingThrowsNotFound() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .get("/api/categories/999")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void insertAsUserNotWork() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(new Category.CategoryRequestBodyPost("New category", "asjdkhaskjdhaskjdhaskjdhaskjdhasdkjashdkjashdkjashdakjsdhask")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    public void insertOneAsAdminWorks() throws Exception {
        long initialSize = categoryRepository.count();
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(new Category.CategoryRequestBodyPost("New category", "asjdkhaskjdhaskjdhaskjdhaskjdhasdkjashdkjashdkjashdakjsdhask")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assert initialSize + 1 == categoryRepository.count();
    }

    @Test
    public void insertOneMissingName() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(new Category.CategoryRequestBodyPost("", "asjdkhaskjdhaskjdhaskjdhaskjdhasdkjashdkjashdkjashdakjsdhask")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void insertOneMissingDescription() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(new Category.CategoryRequestBodyPost("asdsadasdsadasdasdasd", "")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void editAsAnonymousFails() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .put("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(validPutPatch))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    public void editAsUserFails() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .put("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(validPutPatch))
                        .header(testUtils.authorization, testUtils.user())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    public void editAsAdminWorks() throws Exception {
        long initialCount = categoryRepository.findByName(validPutPatch.getName()).size();
        mvc.perform(
                MockMvcRequestBuilders
                        .put("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(validPutPatch))
                        .header(testUtils.authorization, testUtils.admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assert initialCount + 1 == categoryRepository.findByName(validPutPatch.getName()).size();
    }

    @Test
    public void editAsAdminPartialNameNotWorks() throws Exception {
        Category.CategoryRequestBodyPutPatch data = validPutPatch.toBuilder().name(null).build();
        mvc.perform(
                MockMvcRequestBuilders
                        .put("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(data))
                        .header(testUtils.authorization, testUtils.admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void editAsAdminPartialDescriptionNotWorks() throws Exception {
        Category.CategoryRequestBodyPutPatch data = validPutPatch.toBuilder().description(null).build();
        mvc.perform(
                MockMvcRequestBuilders
                        .put("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(data))
                        .header(testUtils.authorization, testUtils.admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void editAsAdminNoId() throws Exception {
        Category.CategoryRequestBodyPutPatch data = validPutPatch.toBuilder().id(null).build();
        mvc.perform(
                MockMvcRequestBuilders
                        .put("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(data))
                        .header(testUtils.authorization, testUtils.admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void editAsAdminNoValidCommentId() throws Exception {
        Category.CategoryRequestBodyPutPatch data = validPutPatch.toBuilder().id(999L).build();
        mvc.perform(
                MockMvcRequestBuilders
                        .put("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(data))
                        .header(testUtils.authorization, testUtils.admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void patchAsAnonymousFails() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .patch("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(validPutPatch))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    public void patchAsUserFails() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .patch("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(validPutPatch))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    public void patchAsAdminWorks() throws Exception {
        long initialCount = categoryRepository.findByName(validPutPatch.getName()).size();
        mvc.perform(
                MockMvcRequestBuilders
                        .patch("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(validPutPatch))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assert initialCount + 1 == categoryRepository.findByName(validPutPatch.getName()).size();
    }

    @Test
    public void patchAsAdminNotFound() throws Exception {
        Category.CategoryRequestBodyPutPatch data = validPutPatch.toBuilder().id(999L).build();
        mvc.perform(
                MockMvcRequestBuilders
                        .patch("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(data))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void patchAsAdminNoContent() throws Exception {
        Category.CategoryRequestBodyPutPatch data = validPutPatch.toBuilder().description(null).name(null).build();
        mvc.perform(
                MockMvcRequestBuilders
                        .patch("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(data))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    public void patchAsAdminModifyName() throws Exception {
        Category.CategoryRequestBodyPutPatch data = validPutPatch.toBuilder().name("asdasdsadsad").description(null).build();
        long initialCount = categoryRepository.findByName(data.getName()).size();
        mvc.perform(
                MockMvcRequestBuilders
                        .patch("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(data))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assert initialCount + 1 == categoryRepository.findByName(data.getName()).size();
    }

    @Test
    public void patchAsAdminModifyDescription() throws Exception {
        Category.CategoryRequestBodyPutPatch data = validPutPatch.toBuilder().description("asdasdsadsadasdasdsadsadasdasdsadsadasdasdsadsad").name(null).build();
        long initialCount = categoryRepository.findByDescription(data.getDescription()).size();
        mvc.perform(
                MockMvcRequestBuilders
                        .patch("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(data))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assert initialCount + 1 == categoryRepository.findByDescription(data.getDescription()).size();
    }

    @Test
    public void patchAsAdminInvalidName() throws Exception {
        Category.CategoryRequestBodyPutPatch data = validPutPatch.toBuilder().name("asd").description(null).build();
        mvc.perform(
                MockMvcRequestBuilders
                        .patch("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(data))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void patchAsAdminInvalidDescription() throws Exception {
        Category.CategoryRequestBodyPutPatch data = validPutPatch.toBuilder().description("asd").build();
        mvc.perform(
                MockMvcRequestBuilders
                        .patch("/api/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(data))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void deleteAsAnonymousForbidden() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .delete("/api/categories/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }


    @Test
    public void deleteAsUserForbidden() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .delete("/api/categories/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(testUtils.authorization, testUtils.user())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    public void deleteAsAdminNotFound() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .delete("/api/categories/999")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    public void deleteAsAdmin() throws Exception {
        long id = 1L;
        assert categoryRepository.findById(id).isPresent();
        mvc.perform(
                MockMvcRequestBuilders
                        .delete("/api/categories/" + id)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(testUtils.authorization, testUtils.admin())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
        assert categoryRepository.findById(id).isEmpty();
    }
}
