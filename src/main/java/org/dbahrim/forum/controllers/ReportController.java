package org.dbahrim.forum.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbahrim.forum.models.Report;
import org.dbahrim.forum.models.User;
import org.dbahrim.forum.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin(origins="*")
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

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
        return reportService.findAll();
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
                                   @RequestBody Report.ReportDto dto) throws ErrorController.NotFoundException {
        return reportService.addCommentReport(id, dto, user);
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
        return reportService.addPostReport(id, dto, user);
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
        return reportService.solve(user, id, dto);
    }
}
