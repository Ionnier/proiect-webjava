package org.dbahrim.forum.services;

import lombok.AllArgsConstructor;
import org.dbahrim.forum.controllers.ErrorController;
import org.dbahrim.forum.controllers.VoteController;
import org.dbahrim.forum.data.CommentRepository;
import org.dbahrim.forum.data.PostRepository;
import org.dbahrim.forum.models.Comment;
import org.dbahrim.forum.models.Post;
import org.dbahrim.forum.models.User;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VoteService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public void voteOn(User user, VoteController.Types type, VoteController.Way way, Long id) throws ErrorController.NotFoundException {

        Post post = null;
        Comment comment = null;

        switch (type) {
            case POST -> post = postRepository.findById(id).orElseThrow(ErrorController.NotFoundException::new);
            case COMMENT -> comment = commentRepository.findById(id).orElseThrow(ErrorController.NotFoundException::new);
        }

        switch (way) {
            case UP -> {
                if (post != null) {
                    post.dislikedBy.remove(user);
                    post.upvotedBy.add(user);
                }
                if (comment != null) {
                    comment.dislikedBy.remove(user);
                    comment.upvotedBy.add(user);
                }
            }
            case DOWN -> {
                if (post != null) {
                    post.upvotedBy.remove(user);
                    post.dislikedBy.add(user);
                }
                if (comment != null) {
                    comment.upvotedBy.remove(user);
                    comment.dislikedBy.add(user);
                }
            }
            case CANCEL -> {
                if (post != null) {
                    post.upvotedBy.remove(user);
                    post.dislikedBy.remove(user);
                }
                if (comment != null) {
                    comment.upvotedBy.remove(user);
                    comment.dislikedBy.remove(user);
                }
            }
        }
        if (post != null) {
            postRepository.save(post);
        }

        if (comment != null) {
            commentRepository.save(comment);
        }
    }
}
