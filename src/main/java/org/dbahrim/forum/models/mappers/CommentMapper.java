package org.dbahrim.forum.models.mappers;

import org.dbahrim.forum.models.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment sourceToDestination(Comment.CommentPost source);
}
