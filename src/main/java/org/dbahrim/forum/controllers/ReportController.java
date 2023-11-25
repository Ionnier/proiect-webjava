package org.dbahrim.forum.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

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
    public Iterable<Report> getAll() {
        return reportRepository.findAll();
    }

    @PostMapping("/comment/{id}")
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
    public Report addPostReport(@AuthenticationPrincipal User user,
                             @PathVariable Long id,
                             @RequestBody Report.ReportDto dto) throws ErrorController.NotFoundException {
        Post post = postRepository.findById(id).orElseThrow(ErrorController.NotFoundException::new);
        Report report = reportMapper.toReport(dto);
        report.createdBy = user;
        report.post = post;
        return reportRepository.save(report);
    }

}
