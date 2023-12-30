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
import org.dbahrim.forum.services.VoteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin(origins="*")
@RequiredArgsConstructor
@RequestMapping("/api/vote")
public class VoteController {
    final VoteService voteService;
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
                                   @PathVariable Long id) throws ErrorController.NotFoundException {
        voteService.voteOn(user, type, way, id);
    }

}
