package org.dbahrim.forum.data;

import org.dbahrim.forum.models.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Long> {

}
