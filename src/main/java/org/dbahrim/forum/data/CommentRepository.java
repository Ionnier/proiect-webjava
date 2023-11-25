package org.dbahrim.forum.data;

import org.dbahrim.forum.models.Comment;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long> {

}
