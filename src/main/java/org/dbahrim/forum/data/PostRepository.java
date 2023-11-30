package org.dbahrim.forum.data;

import org.dbahrim.forum.models.Comment;
import org.dbahrim.forum.models.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {
    List<Post> findByContent(String content);
}
