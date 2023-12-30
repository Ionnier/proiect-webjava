package org.dbahrim.forum.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.dbahrim.forum.models.Post;
import org.dbahrim.forum.models.Report;
import org.dbahrim.forum.models.User;
import org.dbahrim.forum.services.LeaderboardService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/top")
@AllArgsConstructor
public class LeaderboardController {
    final LeaderboardService leaderboardService;

    @GetMapping("/users")
    @Operation(summary = "Get top users")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class) )
                }
            ),
            @ApiResponse(
                responseCode = "400",
                content = @Content
            ),
        }
    )
    ResponseEntity<List<User>> topUsers(@Parameter(description = "today/week/month") @RequestParam(required = false, name ="when") String when) throws ErrorController.BadRequest {
        return new ResponseEntity<>(leaderboardService.topUsers(processWhen(when)), HttpStatusCode.valueOf(200));
    }

    @GetMapping("/posts")
    @Operation(summary = "Get top posts")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = Post.class) )
                }
            ),
            @ApiResponse(
                responseCode = "400",
                content = @Content
            ),
        }
    )
    ResponseEntity<List<Post>> topPosts(@Parameter(description = "today/week/month") @RequestParam(required = false, name = "when") String when) throws ErrorController.BadRequest {
        return new ResponseEntity<>(leaderboardService.topPosts(processWhen(when)), HttpStatusCode.valueOf(200));
    }

    Long processWhen(String when) throws ErrorController.BadRequest {
        if (when == null) {
            return null;
        }
        if (StringUtils.startsWithIgnoreCase("today", when)) {
            return TimeUnit.DAYS.toMillis(1);
        } else if (StringUtils.startsWithIgnoreCase("week", when)) {
            return TimeUnit.DAYS.toMillis(7);
        } else if (StringUtils.startsWithIgnoreCase("month", when)) {
            return TimeUnit.DAYS.toMillis(30);
        }
        throw new ErrorController.BadRequest();
    }
}
