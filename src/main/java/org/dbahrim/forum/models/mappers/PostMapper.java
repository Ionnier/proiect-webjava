package org.dbahrim.forum.models.mappers;

import org.dbahrim.forum.models.Post;
import org.dbahrim.forum.models.Report;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    Post toPost(Post.PostPostRequest dto);
    Post toPost(Post.PostPutPatchRequest dto);
}
