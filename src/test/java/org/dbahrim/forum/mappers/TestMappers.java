package org.dbahrim.forum.mappers;

import org.dbahrim.forum.models.Comment;
import org.dbahrim.forum.models.Post;
import org.dbahrim.forum.models.mappers.CommentMapper;
import org.dbahrim.forum.models.mappers.EventMapper;
import org.dbahrim.forum.models.mappers.PostMapper;
import org.dbahrim.forum.models.mappers.ReportMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestMappers {
    @Autowired
    CommentMapper commentMapper;

    @Autowired
    EventMapper eventMapper;

    @Autowired
    PostMapper postMapper;

    @Autowired
    ReportMapper reportMapper;

    @Test
    void testCommentNullReturnNull() throws Exception {
        Assertions.assertNull(commentMapper.sourceToDestination(null));
    }

    @Test
    void testEventMapperNullReturnNull() throws Exception {
        Assertions.assertNull(eventMapper.sourceToDestination(null));
    }

    @Test
    void testPostMapperNullReturnNull() throws Exception {
        Assertions.assertNull(postMapper.toPost((Post.PostPostRequest) null));
        Assertions.assertNull(postMapper.toPost((Post.PostPutPatchRequest)null));
    }

    @Test
    void testPostMapper() throws Exception {
        Assertions.assertNotNull(postMapper.toPost(new Post.PostPostRequest(null, "asd", "asd")));
        Assertions.assertNotNull(postMapper.toPost(new Post.PostPutPatchRequest(null, null, "asd", "asd")));
    }

    @Test
    void testCommentMapper() throws Exception {
        Comment.CommentPost source = new Comment.CommentPost();
        source.content = "asd";
        Assertions.assertNotNull(commentMapper.sourceToDestination(source));
    }

    @Test
    void testReportMappter() throws Exception {
        Assertions.assertNull(reportMapper.toReport(null));
    }


}
