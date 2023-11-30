package org.dbahrim.forum.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbahrim.forum.data.CommentRepository;
import org.dbahrim.forum.data.PostRepository;
import org.dbahrim.forum.models.Comment;
import org.dbahrim.forum.models.Post;
import org.dbahrim.forum.models.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@Slf4j
@CrossOrigin(origins="*")
@RequiredArgsConstructor
@RequestMapping("/api/vote")
public class VoteController {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public enum Types {
        COMMENT, POST
    }

    public enum Way {
        UP, DOWN, CANCEL
    }

    @PostMapping("/{type}/{way}/{id}")
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Vote on a post or comment")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content
                    )
            }
    )
    public void vote(@AuthenticationPrincipal User user,
                                   @PathVariable Types type,
                                   @PathVariable Way way,
                                   @PathVariable Long id) throws ErrorController.NotFoundException, JsonProcessingException, ErrorController.BadRequest {
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
