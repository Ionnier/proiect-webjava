package org.dbahrim.forum.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbahrim.forum.data.CommentRepository;
import org.dbahrim.forum.data.PostRepository;
import org.dbahrim.forum.data.ReportRepository;
import org.dbahrim.forum.models.Comment;
import org.dbahrim.forum.models.Post;
import org.dbahrim.forum.models.Report;
import org.dbahrim.forum.models.User;
import org.dbahrim.forum.models.mappers.CommentMapper;
import org.dbahrim.forum.models.mappers.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@CrossOrigin(origins="*")
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Autowired
    private ReportMapper reportMapper;

    @GetMapping
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Get all reports")
    @ApiResponses(
        value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Report.class) )
                    }
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = @Content
            )
        }
    )
    public Iterable<Report> getAll() {
        return reportRepository.findAll();
    }

    @PostMapping("/comment/{id}")
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Add a report to a comment")
    @ApiResponses(
        value = {
                @ApiResponse(
                        responseCode = "200",
                        content = {
                                @Content(mediaType = "application/json", schema = @Schema(implementation = Report.class) )
                        }
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
    public Report addCommentReport(@AuthenticationPrincipal User user,
                                   @PathVariable Long id,
                                   @RequestBody Report.ReportDto dto, ObjectMapper objectMapper) throws ErrorController.NotFoundException, JsonProcessingException {
        Comment comment = commentRepository.findById(id).orElseThrow(ErrorController.NotFoundException::new);
        Report report = reportMapper.toReport(dto);
        report.createdBy = user;
        report.comment = comment;
        System.out.println(objectMapper.writeValueAsString(report));
        return reportRepository.save(report);
    }

    @PostMapping("/post/{id}")
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Add a report to a post")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = Report.class) )
                }
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
    public Report addPostReport(@AuthenticationPrincipal User user,
                             @PathVariable Long id,
                             @RequestBody Report.ReportDto dto) throws ErrorController.NotFoundException {
        Post post = postRepository.findById(id).orElseThrow(ErrorController.NotFoundException::new);
        Report report = reportMapper.toReport(dto);
        report.createdBy = user;
        report.post = post;
        return reportRepository.save(report);
    }

    @PatchMapping("/{id}")
    @Transactional
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Solve a report")
    @ApiResponses(
        value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Report.class) )
                    }
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
    public Report resolveReport(@AuthenticationPrincipal User user,
                                @PathVariable Long id,
                                @RequestBody Report.ReportResolution dto) throws ErrorController.NotFoundException {
        Report report = reportRepository.findById(id).orElseThrow(ErrorController.NotFoundException::new);
        report.resolvedBy = user;
        report.resolution = dto.resolution;
        report.message = dto.message;
        switch (report.resolution) {
            case CLEANED -> {
                if (report.comment != null) {
                    report.comment.setContent(dto.message);
                    commentRepository.save(report.comment);
                } else {
                    report.post.content = dto.message;
                    postRepository.save(report.post);
                }
            }
            case DELETED -> {
                if (report.comment != null) {
                    Post post = postRepository
                            .findById(report.comment.postId)
                            .orElseThrow(ErrorController.NotFoundException::new);
                    post.comments.removeIf(e -> Objects.equals(e.getId(), report.comment.getId()));
                    postRepository.save(post);
                } else {
                    postRepository.deleteById(report.post.id);
                }
            }
        }
        return reportRepository.save(report);
    }
}
