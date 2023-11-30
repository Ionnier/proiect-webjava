package org.dbahrim.forum.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.dbahrim.forum.data.CategoryRepository;
import org.dbahrim.forum.models.Category;
import org.dbahrim.forum.models.Post;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.dbahrim.forum.controllers.ErrorController.NotFoundException;

@RestController
@Slf4j
@CrossOrigin(origins="*")
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryRepository categoryRepository;

    @GetMapping
    @Operation(summary = "Get all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Post.class)) })})
    public Iterable<Category> getAll() {
        return categoryRepository.findAll();
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get one category")
    public Category getOne(@PathVariable Long id) throws NotFoundException {
        return categoryRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @PostMapping
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Add new category")
    public Category insert(@RequestBody @Valid Category.CategoryRequestBodyPost categoryRequestBodyPost) {
        return categoryRepository.save(new Category(categoryRequestBodyPost));
    }

    @PutMapping
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Fully edit existing category")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Successful",
                content = {
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Category.class)
                        )
                }
            ),
            @ApiResponse(
                responseCode = "404",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "400",
                content = @Content
            )
        }
    )
    public Category edit(@RequestBody @Valid Category.CategoryRequestBodyPutPatch categoryRequestBodyPutPatch) throws NotFoundException, ErrorController.BadRequest {
        Category category = categoryRepository.findById(categoryRequestBodyPutPatch.getId()).orElseThrow(NotFoundException::new);
        return categoryRepository.save(category.fromPatch(categoryRequestBodyPutPatch, false).orElseThrow(ErrorController.BadRequest::new));
    }

    @PatchMapping
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Partial edit existing category")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Successful",
                content = {
                    @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Category.class)
                    )
                }
            ),
            @ApiResponse(
                responseCode = "404",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "400",
                content = @Content
            )
        }
    )
    public Category patch(@RequestBody Category.CategoryRequestBodyPutPatch categoryRequestBodyPutPatch) throws NotFoundException, ErrorController.BadRequest {
        Category category = categoryRepository.findById(categoryRequestBodyPutPatch.getId()).orElseThrow(NotFoundException::new);
        return categoryRepository.save(category.fromPatch(categoryRequestBodyPutPatch, true).orElseThrow(ErrorController.BadRequest::new));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Delete category")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "204",
                content = @Content
            )
        }
    )
    public void delete(@PathVariable Long id){
        categoryRepository.deleteById(id);
    }
}
