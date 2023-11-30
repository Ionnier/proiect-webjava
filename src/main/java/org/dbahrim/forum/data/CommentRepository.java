package org.dbahrim.forum.data;

import org.dbahrim.forum.models.Comment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findByContent(String content);
}
