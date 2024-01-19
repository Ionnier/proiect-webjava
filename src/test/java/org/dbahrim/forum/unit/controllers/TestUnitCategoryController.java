package org.dbahrim.forum.unit.controllers;

import io.jsonwebtoken.lang.Assert;
import org.dbahrim.forum.configuration.security.UserRequest;
import org.dbahrim.forum.controllers.ErrorController;
import org.dbahrim.forum.data.CategoryRepository;
import org.dbahrim.forum.models.Category;
import org.dbahrim.forum.services.AuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class TestUnitCategoryController {
    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    org.dbahrim.forum.controllers.CategoryController categoryController;


    @Mock
    Category category;

    @Test
    @DisplayName("Test get all")
    public void test1() throws Exception {
        when(categoryRepository.findAll()).thenReturn(List.of());
        Assertions.assertEquals(categoryController.getAll().spliterator().estimateSize(), 0L);
        verify(categoryRepository, times(1)).findAll();
    }


    @Test
    @DisplayName("Test get one")
    public void test2() throws Exception {
        long id = 1L;
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Assertions.assertEquals(categoryController.getOne(id), category);
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Test get one exception")
    public void test3() throws Exception {
        long id = 1L;
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(ErrorController.NotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                categoryController.getOne(id);
            }
        });
        verify(categoryRepository, times(1)).findById(id);
    }

    @Mock
    Category.CategoryRequestBodyPost categoryRequestBodyPost;

    @Test
    @DisplayName("Test insert")
    public void test4() throws Exception {
        when(categoryRepository.save(any())).thenReturn(category);
        categoryController.insert(categoryRequestBodyPost);
        verify(categoryRepository, times(1)).save(any());
    }

    @Mock
    Category.CategoryRequestBodyPutPatch categoryRequestBodyPutPatch;

    @Test
    @DisplayName("Test edit category throw")
    public void test5() throws Exception {
        long id = 1L;
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        when(categoryRequestBodyPutPatch.getId()).thenReturn(id);
        Assertions.assertThrows(ErrorController.NotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                categoryController.edit(categoryRequestBodyPutPatch);
            }
        });
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Test edit category")
    public void test6() throws Exception {
        long id = 1L;
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRequestBodyPutPatch.getId()).thenReturn(id);
        when(categoryRepository.save(any())).thenReturn(category);
        when(category.fromPatch(any(), any())).thenReturn(Optional.of(category));
        categoryController.edit(categoryRequestBodyPutPatch);
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Test from patch throw")
    public void test7() throws Exception {
        long id = 1L;
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRequestBodyPutPatch.getId()).thenReturn(id);
        when(category.fromPatch(any(), any())).thenReturn(Optional.empty());
        Assertions.assertThrows(ErrorController.BadRequest.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                categoryController.edit(categoryRequestBodyPutPatch);
            }
        });
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Test delete")
    public void test8() throws Exception {
        long id = 1L;
        categoryController.delete(id);
        verify(categoryRepository, times(1)).deleteById(id);
    }
}