package org.dbahrim.forum.data;

import io.swagger.v3.oas.annotations.Hidden;
import org.dbahrim.forum.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Hidden
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByContent(String content);
}
