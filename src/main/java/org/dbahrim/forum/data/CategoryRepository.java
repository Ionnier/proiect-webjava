package org.dbahrim.forum.data;

import io.swagger.v3.oas.annotations.Hidden;
import org.dbahrim.forum.models.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
@Hidden
public interface CategoryRepository extends CrudRepository<Category, Long> {
    List<Category> findByName(String name);
    List<Category> findByDescription(String description);
}
