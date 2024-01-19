package org.dbahrim.forum.services;

import org.dbahrim.forum.data.CommentRepository;
import org.dbahrim.forum.data.PostRepository;
import org.dbahrim.forum.data.ReportRepository;
import org.dbahrim.forum.data.UserRepository;
import org.dbahrim.forum.models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestLeaderboardService {
    @Mock
    UserRepository userRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ReportRepository reportRepository;

    @InjectMocks
    LeaderboardService leaderboardService;

    final Category category = new Category(1L, "ASD", "asd", List.of());

    User getUser1() {
        User user1 = new User("asd", "asd@cti.ro");
        user1.setId(1L);
        return user1;
    }

    User getUser2() {
        User user1 = new User("asd", "asd@cti.ro");
        user1.setId(2L);
        return user1;
    }

    User getUser3() {
        User user1 = new User("asd", "asd@cti.ro");
        user1.setId(3L);
        return user1;
    }

    User getUser4() {
        User user1 = new User("asd", "asdasd@cti.ro");
        user1.setId(4L);
        return user1;
    }

    User getUser5() {
        User user1 = new User("asd", "asdasdasd@cti.ro");
        user1.setId(5L);
        return user1;
    }

    User getUser6() {
        User user1 = new User("asd", "asdasdasd@cti.ro");
        user1.setId(6L);
        return user1;
    }

    @Test
    void emptyReturnsEmpty() {
        when(postRepository.findAll()).thenReturn(List.of());
        when(commentRepository.findAll()).thenReturn(List.of());
        when(reportRepository.findAll()).thenReturn(List.of());
        assert leaderboardService.topUsers(null).size() == 0;
    }

    @Test
    void postReturnsOne() {
        Post post = new Post(category, getUser1(), "asdasdasdasdsa", "sadasdsadsad");
        post.createdAt = Date.from(Instant.now());
        when(postRepository.findAll()).thenReturn(List.of(post));
        when(commentRepository.findAll()).thenReturn(List.of());
        when(reportRepository.findAll()).thenReturn(List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.of(getUser1()));
        assert leaderboardService.topUsers(null).size() == 1;
    }

    @Test
    // double for comment
    void postReturnsZeroFilteredByCreatedDate() {
        Post post = new Post(category, getUser1(), "asdasdasdasdsa", "sadasdsadsad");
        post.createdAt = Date.from(Instant.ofEpochMilli(Instant.now().toEpochMilli() - 500));
        when(postRepository.findAll()).thenReturn(List.of(post));
        when(commentRepository.findAll()).thenReturn(List.of());
        when(reportRepository.findAll()).thenReturn(List.of());
        assert leaderboardService.topUsers(1L).size() == 0;
    }

    @Test
    void postReturnsFirst() {
        Post post = new Post(category, getUser1(), "asdasdasdasdsa", "sadasdsadsad");
        Post post3 = new Post(category, getUser1(), "asdasdasdasdsa", "sadasdsadsad");
        Post post2 = new Post(category, getUser2(), "asdasdasdasdsa", "sadasdsadsad");
        List<Post> postList = List.of(post, post3, post2);
        for (Post poste: postList) {
            poste.createdAt = Date.from(Instant.now());
        }
        when(postRepository.findAll()).thenReturn(postList);
        when(commentRepository.findAll()).thenReturn(List.of());
        when(reportRepository.findAll()).thenReturn(List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.of(getUser1()));
        List<User> result = leaderboardService.topUsers(null);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(1, result.get(0).getId());
    }

    @Test
    void postReturnsSecond() {
        Post post = new Post(category, getUser1(), "asdasdasdasdsa", "sadasdsadsad");
        Post post3 = new Post(category, getUser2(), "asdasdasdasdsa", "sadasdsadsad");
        Post post2 = new Post(category, getUser2(), "asdasdasdasdsa", "sadasdsadsad");
        List<Post> postList = List.of(post, post3, post2);
        for (Post poste: postList) {
            poste.createdAt = Date.from(Instant.now());
        }
        when(postRepository.findAll()).thenReturn(postList);
        when(commentRepository.findAll()).thenReturn(List.of());
        when(reportRepository.findAll()).thenReturn(List.of());
        when(userRepository.findById(2L)).thenReturn(Optional.of(getUser2()));
        List<User> result = leaderboardService.topUsers(null);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(2, result.get(0).getId());
    }

    @Mock
    Report report1;

    @Mock
    Report report2;

    @Mock
    Report report3;

    @Mock
    Comment comment;

    @Mock
    Post post;


    @Mock
    Comment comment2;

    @Mock
    Comment comment3;

    @Mock
    Comment comment4;


    @Test
    void testReportIteration() {
        report1.createdBy = getUser1();
        report2.createdBy = getUser1();
        report3.createdBy = getUser1();
        report1.resolution = Report.Resolution.NOT_VERIFIED;
        report2.comment = comment;
        when(comment.getUser()).thenReturn(getUser2());
        post.user = getUser2();
        report3.post = post;
        when(comment2.getUser()).thenReturn(getUser4());
        when(comment3.getUser()).thenReturn(getUser5());
        when(comment4.getUser()).thenReturn(getUser6());
        comment2.createdAt = new Date();
        comment3.createdAt = new Date();
        comment4.createdAt = new Date();
        when(postRepository.findAll()).thenReturn(List.of());
        when(commentRepository.findAll()).thenReturn(List.of(comment2, comment3, comment4));
        when(reportRepository.findAll()).thenReturn(List.of(report1, report2, report3));
        when(userRepository.findById(any())).thenReturn(Optional.of(getUser2()));
        List<User> result = leaderboardService.topUsers(null);
        Assertions.assertEquals(5, result.size());
        Assertions.assertEquals(2, result.get(0).getId());
    }
}
