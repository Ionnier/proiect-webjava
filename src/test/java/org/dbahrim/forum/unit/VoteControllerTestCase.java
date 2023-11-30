package org.dbahrim.forum.unit;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.dbahrim.forum.controllers.ErrorController;
import org.dbahrim.forum.controllers.VoteController;
import org.dbahrim.forum.data.CommentRepository;
import org.dbahrim.forum.data.PostRepository;
import org.dbahrim.forum.models.Comment;
import org.dbahrim.forum.models.Post;
import org.dbahrim.forum.models.User;
import org.dbahrim.forum.models.Vote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Type;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VoteControllerTestCase {
    VoteController voteController;

    @Test
    void test_upvote_post(
            @Mock PostRepository postRepository,
            @Mock CommentRepository commentRepository,
            @Mock User user,
            @Mock Post post,
            @Mock Comment comment,
            @Mock HashSet<User> dislikedBy,
            @Mock HashSet<User> upvotedBy
    ) throws Exception {
        voteController = new VoteController(postRepository, commentRepository);
        post.dislikedBy = dislikedBy;
        post.upvotedBy = upvotedBy;
        lenient().when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        lenient().when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        voteController.vote(user, VoteController.Types.POST, VoteController.Way.UP, 1L);
        verify(postRepository, times(1)).save(post);
        verify(commentRepository, times(0)).save(comment);
        verify(dislikedBy, times(1)).remove(user);
        verify(upvotedBy, times(1)).add(user);
    }

    @Test
    void test_downvote_post(
            @Mock PostRepository postRepository,
            @Mock CommentRepository commentRepository,
            @Mock User user,
            @Mock Post post,
            @Mock Comment comment,
            @Mock HashSet<User> dislikedBy,
            @Mock HashSet<User> upvotedBy
    ) throws Exception {
        voteController = new VoteController(postRepository, commentRepository);
        post.dislikedBy = dislikedBy;
        post.upvotedBy = upvotedBy;
        lenient().when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        lenient().when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        voteController.vote(user, VoteController.Types.POST, VoteController.Way.DOWN, 1L);
        verify(postRepository, times(1)).save(post);
        verify(commentRepository, times(0)).save(comment);
        verify(dislikedBy, times(1)).add(user);
        verify(upvotedBy, times(1)).remove(user);
    }

    @Test
    void test_cancel_post(
            @Mock PostRepository postRepository,
            @Mock CommentRepository commentRepository,
            @Mock User user,
            @Mock Post post,
            @Mock Comment comment,
            @Mock HashSet<User> dislikedBy,
            @Mock HashSet<User> upvotedBy
    ) throws Exception {
        voteController = new VoteController(postRepository, commentRepository);
        post.dislikedBy = dislikedBy;
        post.upvotedBy = upvotedBy;
        lenient().when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        lenient().when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        voteController.vote(user, VoteController.Types.POST, VoteController.Way.CANCEL, 1L);
        verify(postRepository, times(1)).save(post);
        verify(commentRepository, times(0)).save(comment);
        verify(dislikedBy, times(1)).remove(user);
        verify(upvotedBy, times(1)).remove(user);
    }

}
