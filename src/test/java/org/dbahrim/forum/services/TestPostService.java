package org.dbahrim.forum.services;

import org.dbahrim.forum.controllers.ErrorController;
import org.dbahrim.forum.controllers.PostController;
import org.dbahrim.forum.data.CategoryRepository;
import org.dbahrim.forum.data.CommentRepository;
import org.dbahrim.forum.data.PostRepository;
import org.dbahrim.forum.models.Category;
import org.dbahrim.forum.models.Comment;
import org.dbahrim.forum.models.Post;
import org.dbahrim.forum.models.User;
import org.dbahrim.forum.models.mappers.CommentMapper;
import org.dbahrim.forum.models.mappers.PostMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import javax.swing.text.html.Option;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestPostService {
    @Mock
    PostRepository postRepository;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    PostMapper postMapper;
    @Mock
    CommentMapper commentMapper;

    @InjectMocks
    PostController postController;

    @Mock(strictness = Mock.Strictness.LENIENT)
    Post post;

    @Mock(strictness = Mock.Strictness.LENIENT)
    Comment comment;

    @Mock(strictness = Mock.Strictness.LENIENT)
    Category category;

    @Mock(strictness = Mock.Strictness.LENIENT)
    User user;

    @Mock(strictness = Mock.Strictness.LENIENT)
    User user2;

    @Mock(strictness = Mock.Strictness.LENIENT)
    Post.PostPostRequest dtoPost;

    @Mock(strictness = Mock.Strictness.LENIENT)
    Post.PostPutPatchRequest dtoPut;

    @Test
    public void testAddPost() throws Exception {
        when(categoryRepository.findById(any())).thenReturn(Optional.of(category));
        when(postMapper.toPost((Post.PostPostRequest) any())).thenReturn(post);
        when(postRepository.save(any())).thenReturn(post);
        postController.insert(dtoPost, user);
    }

    @Test
    public void testAddPostNotFound() throws Exception {
        when(categoryRepository.findById(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(ErrorController.NotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                postController.insert(dtoPost, user);
            }
        });
    }

    @Test
    public void testPostComment() throws Exception {
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(commentMapper.sourceToDestination(any())).thenReturn(comment);
        when(postRepository.save(any())).thenReturn(post);
        postController.comment(1L, new Comment.CommentPost(), user);
    }

    @Test
    public void testPostCommentThrowNoCategory() throws Exception {
        when(postRepository.findById(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(ErrorController.NotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                postController.comment(1L, new Comment.CommentPost(), user);
            }
        });
    }

    @Test
    public void testEdit() throws Exception {
        post.user = user;
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(user.getId()).thenReturn(1L);
        when(user2.getId()).thenReturn(2L);
        Assertions.assertThrows(AccessDeniedException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                postController.edit(dtoPut, user2);
            }
        });
        post.user = null;
    }

    @Test
    public void testValidEdit() throws Exception {
        post.user = user;
        dtoPut.id = 1L;
        post.id = 1L;
        dtoPut.title="adsasd";
        dtoPut.content = "asdsad";
        dtoPut.categoryId = 3L;
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(categoryRepository.findById(any())).thenReturn(Optional.of(category));
        when(dtoPut.getId()).thenReturn(1L);
        when(post.fromPatch(any(), any(), any())).thenReturn(Optional.of(post));
        postController.edit(dtoPut, user);
        dtoPut.id = null;
        post.id = null;
        post.user = null;
        dtoPut.title= null;
    }

    @Test
    public void testPatch() throws Exception {
        post.user = user;
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(user.getId()).thenReturn(1L);
        when(user2.getId()).thenReturn(2L);
        Assertions.assertThrows(AccessDeniedException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                postController.patch(dtoPut, user2);
            }
        });
        post.user = null;
    }

    @Test
    public void testValidPatch() throws Exception {
        post.user = user;
        dtoPut.id = 1L;
        post.id = 1L;
        dtoPut.title="adsasd";
        dtoPut.content = "asdsad";
        dtoPut.categoryId = 3L;
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(categoryRepository.findById(any())).thenReturn(Optional.of(category));
        when(dtoPut.getId()).thenReturn(1L);
        when(post.fromPatch(any(), any(), any())).thenReturn(Optional.of(post));
        postController.patch(dtoPut, user);
        dtoPut.id = null;
        post.id = null;
        post.user = null;
        dtoPut.title= null;
    }

    @Test
    public void testDeleteAccessDenied() throws Exception {
        post.user = user;
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(user.getId()).thenReturn(1L);
        when(user2.getId()).thenReturn(2L);
        Assertions.assertThrows(AccessDeniedException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                postController.delete(1L, user2);
            }
        });
        post.user = null;
    }

    @Test
    public void testDelete() throws Exception {
        post.user = user;
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(user.getId()).thenReturn(1L);
        postController.delete(1L, user);
        post.user = null;
    }
}
