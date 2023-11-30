package org.dbahrim.forum.data;

import org.dbahrim.forum.models.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface CategoryRepository extends CrudRepository<Category, Long> {
    List<Category> findByName(String name);
    List<Category> findByDescription(String description);
}
