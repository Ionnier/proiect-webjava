package org.dbahrim.forum.unit;


import org.dbahrim.forum.controllers.ErrorController;
import org.dbahrim.forum.controllers.VoteController;
import org.dbahrim.forum.data.CommentRepository;
import org.dbahrim.forum.data.PostRepository;
import org.dbahrim.forum.models.Comment;
import org.dbahrim.forum.models.Post;
import org.dbahrim.forum.models.User;
import org.dbahrim.forum.services.VoteService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VoteServiceTest {
    @InjectMocks
    VoteService voteService;

    @Mock
    PostRepository postRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    User user;

    @InjectMocks
    Post post;

    @InjectMocks
    Comment comment;

    @Mock HashSet<User> dislikedBy;
    @Mock HashSet<User> upvotedBy;

    @Test
    void testPostNotFound() throws Exception {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(ErrorController.NotFoundException.class, () -> {
            voteService.voteOn(user, VoteController.Types.POST, VoteController.Way.DOWN, 1L);
        });
    }

    @Test
    void testCommentNotFound() throws Exception {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(ErrorController.NotFoundException.class, () -> {
            voteService.voteOn(user, VoteController.Types.COMMENT, VoteController.Way.DOWN, 1L);
        });
    }

    @Test
    void testDownVotePost() throws Exception {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        assert(post.dislikedBy.size() == 0);
        assert(post.upvotedBy.size() == 0);
        voteService.voteOn(user, VoteController.Types.POST, VoteController.Way.DOWN, 1L);
        assert(post.dislikedBy.size() == 1);
        assert(post.upvotedBy.size() == 0);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void testUpVotePost() throws Exception {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        assert(post.dislikedBy.size() == 0);
        assert(post.upvotedBy.size() == 0);
        voteService.voteOn(user, VoteController.Types.POST, VoteController.Way.UP, 1L);
        assert(post.dislikedBy.size() == 0);
        assert(post.upvotedBy.size() == 1);
        verify(postRepository, times(1)).save(any());
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    void testDownVoteUpVote() throws Exception {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        assert(post.dislikedBy.size() == 0);
        assert(post.upvotedBy.size() == 0);
        voteService.voteOn(user, VoteController.Types.POST, VoteController.Way.UP, 1L);
        assert(post.dislikedBy.size() == 0);
        assert(post.upvotedBy.size() == 1);
        voteService.voteOn(user, VoteController.Types.POST, VoteController.Way.DOWN, 1L);
        assert(post.dislikedBy.size() == 1);
        assert(post.upvotedBy.size() == 0);
        verify(postRepository, times(2)).save(post);
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    void testCancelDownVote() throws Exception {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        assert(post.dislikedBy.size() == 0);
        assert(post.upvotedBy.size() == 0);
        voteService.voteOn(user, VoteController.Types.POST, VoteController.Way.DOWN, 1L);
        assert(post.dislikedBy.size() == 1);
        assert(post.upvotedBy.size() == 0);
        voteService.voteOn(user, VoteController.Types.POST, VoteController.Way.CANCEL, 1L);
        assert(post.dislikedBy.size() == 0);
        assert(post.upvotedBy.size() == 0);
        verify(postRepository, times(2)).save(post);
        verify(commentRepository, times(0)).save(any());
    }

    // duplicate tests for comments :shrug:

}
