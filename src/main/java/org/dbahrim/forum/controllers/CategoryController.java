package org.dbahrim.forum.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbahrim.forum.data.CategoryRepository;
import org.dbahrim.forum.models.Category;
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
    public Iterable<Category> getAll() {
        return categoryRepository.findAll();
    }


    @GetMapping("/{id}")
    public Category getOne(@PathVariable Long id) throws NotFoundException {
        return categoryRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @PostMapping
    public Category insert(@RequestBody @Valid Category.CategoryRequestBodyPost categoryRequestBodyPost) {
        return categoryRepository.save(new Category(categoryRequestBodyPost));
    }

    @PutMapping
    public Category edit(@RequestBody @Valid Category.CategoryRequestBodyPutPatch categoryRequestBodyPutPatch) throws NotFoundException {
        Category category = categoryRepository.findById(categoryRequestBodyPutPatch.getId()).orElseThrow(NotFoundException::new);
        return categoryRepository.save(category.fromPatch(categoryRequestBodyPutPatch, false).orElseThrow(NotFoundException::new));
    }

    @PatchMapping
    public Category patch(@RequestBody Category.CategoryRequestBodyPutPatch categoryRequestBodyPutPatch) throws NotFoundException {
        Category category = categoryRepository.findById(categoryRequestBodyPutPatch.getId()).orElseThrow(NotFoundException::new);
        return categoryRepository.save(category.fromPatch(categoryRequestBodyPutPatch, true).orElseThrow(NotFoundException::new));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        categoryRepository.deleteById(id);
    }
}
