package org.dbahrim.forum.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbahrim.forum.controllers.ErrorController;
import org.dbahrim.forum.data.CommentRepository;
import org.dbahrim.forum.data.PostRepository;
import org.dbahrim.forum.data.ReportRepository;
import org.dbahrim.forum.data.UserRepository;
import org.dbahrim.forum.models.Comment;
import org.dbahrim.forum.models.Post;
import org.dbahrim.forum.models.Report;
import org.dbahrim.forum.models.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@AllArgsConstructor
@Slf4j
public class LeaderboardService {
    static final int maximum = 5;
    final UserRepository userRepository;
    final PostRepository postRepository;
    final CommentRepository commentRepository;
    final ReportRepository reportRepository;

    public List<User> topUsers(Long offsetInterval) {
        if (offsetInterval == null) {
            offsetInterval = TimeUnit.DAYS.toMillis(1);
        }
        Date genesisDate = Date.from(Instant.now().minusMillis(offsetInterval));
        List<Post> posts = postRepository.findAll().stream().filter(e -> e.createdAt.after(genesisDate)).toList();
        List<Comment> comments = commentRepository.findAll().stream().filter(e -> e.createdAt.after(genesisDate)).toList();
        SortedMap<Long, Long> topUsers = generateMap();
        posts.forEach(e -> {
            incrementUser(topUsers, e.user, 5.0);
            e.upvotedBy.forEach(t -> incrementUser(topUsers, t, 0.25));
            e.dislikedBy.forEach(t -> incrementUser(topUsers, t, 0.25));
        });
        comments.forEach(e -> incrementUser(topUsers, e.getUser(), 1.0));
        for (Report e: reportRepository.findAll()) {
            incrementUser(topUsers, e.createdBy, 0.25);
            if (e.resolution == Report.Resolution.NOT_VALID || e.resolution == Report.Resolution.NOT_VERIFIED) {
                continue;
            }
            if (e.comment != null) {
                incrementUser(topUsers, e.comment.getUser(), -5.0);
            }
            if (e.post != null) {
                incrementUser(topUsers, e.post.user, -5.0);
            }
        }
        ArrayList<User> users = new ArrayList<>(List.of());
        for (Map.Entry<Long, Long> entry : topUsers.entrySet().stream().sorted((o1, o2) -> (int) (o2.getValue() - o1.getValue())).toList()) {
            users.add(userRepository.findById(entry.getKey()).orElse(new User("asd", "asd@asd.com")));
            if (users.size() >= maximum) {
                break;
            }
        }
        return users;
    }

    public List<Post> topPosts(Long offsetInterval) {
        Date genesisDate;
        if (offsetInterval != null) {
            genesisDate = Date.from(Instant.now().minusMillis(offsetInterval));
        } else {
            genesisDate = null;
        }
        List<Post> posts = postRepository.findAll().stream().filter(e -> {
            if (genesisDate == null) {
                return true;
            }
            return e.createdAt.after(genesisDate);
        }).toList();
        SortedMap<Long, Long> topPosts = generateMap();
        posts.forEach(e -> {
            incrementIds(topPosts, e.id, 0.25 * e.content.length());
            incrementIds(topPosts, e.id, (double) e.upvotedBy.size());
            incrementIds(topPosts, e.id, 0.25 * e.dislikedBy.size());
            incrementIds(topPosts, e.id, 5.0 * e.comments.size());
        });
        ArrayList<Post> postArrayList = new ArrayList<>(List.of());
        for (Map.Entry<Long, Long> entry : topPosts.entrySet().stream().sorted((o1, o2) -> (int) (o2.getValue() - o1.getValue())).toList()) {
            try {
                postArrayList.add(postRepository.findById(entry.getKey()).orElseThrow(ErrorController.NotFoundException::new));
            } catch (ErrorController.NotFoundException e) {
                throw new RuntimeException(e);
            }
            if (postArrayList.size() >= maximum) {
                break;
            }
        }
        return postArrayList;
    }

    private void incrementUser(SortedMap<Long, Long> topUsers, @NotNull User user, double v) {
        incrementIds(topUsers, user.getId(), v);
    }

    SortedMap<Long, Long> generateMap() {
        return new TreeMap<>((o1, o2) -> (int) (o2 - o1));
    }

    void incrementIds(SortedMap<Long, Long> map, Long value, Double points) {
        Long currentPoints = map.get(value);
        Long convertedPoints = Math.round(points * 1000);
        if (currentPoints == null) {
            map.put(value, convertedPoints);
            return;
        }
        map.put(value, convertedPoints + currentPoints);
    }
}
