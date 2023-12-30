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
import org.dbahrim.forum.services.VoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Type;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VoteControllerTestCase {
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
    void testDownVotePost() throws Exception {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        assert(post.dislikedBy.size() == 0);
        assert(post.upvotedBy.size() == 0);
        voteService.voteOn(user, VoteController.Types.POST, VoteController.Way.DOWN, 1L);
        assert(post.dislikedBy.size() == 1);
        assert(post.upvotedBy.size() == 0);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void testUpVotePost() throws Exception {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        assert(post.dislikedBy.size() == 0);
        assert(post.upvotedBy.size() == 0);
        voteService.voteOn(user, VoteController.Types.POST, VoteController.Way.UP, 1L);
        assert(post.dislikedBy.size() == 0);
        assert(post.upvotedBy.size() == 1);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void testDownVoteUpVote() throws Exception {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        assert(post.dislikedBy.size() == 0);
        assert(post.upvotedBy.size() == 0);
        voteService.voteOn(user, VoteController.Types.POST, VoteController.Way.UP, 1L);
        assert(post.dislikedBy.size() == 0);
        assert(post.upvotedBy.size() == 1);
        voteService.voteOn(user, VoteController.Types.POST, VoteController.Way.DOWN, 1L);
        assert(post.dislikedBy.size() == 1);
        assert(post.upvotedBy.size() == 0);
        verify(postRepository, times(2)).save(post);
    }

    @Test
    void testCancelDownVote() throws Exception {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        assert(post.dislikedBy.size() == 0);
        assert(post.upvotedBy.size() == 0);
        voteService.voteOn(user, VoteController.Types.POST, VoteController.Way.DOWN, 1L);
        assert(post.dislikedBy.size() == 1);
        assert(post.upvotedBy.size() == 0);
        voteService.voteOn(user, VoteController.Types.POST, VoteController.Way.CANCEL, 1L);
        assert(post.dislikedBy.size() == 0);
        assert(post.upvotedBy.size() == 0);
        verify(postRepository, times(2)).save(post);
    }

}
