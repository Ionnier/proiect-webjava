package org.dbahrim.forum.services;

import lombok.AllArgsConstructor;
import org.dbahrim.forum.controllers.ErrorController;
import org.dbahrim.forum.data.CommentRepository;
import org.dbahrim.forum.data.PostRepository;
import org.dbahrim.forum.data.ReportRepository;
import org.dbahrim.forum.models.Comment;
import org.dbahrim.forum.models.Post;
import org.dbahrim.forum.models.Report;
import org.dbahrim.forum.models.User;
import org.dbahrim.forum.models.mappers.ReportMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ReportMapper reportMapper;

    public Report addCommentReport(Long id, Report.ReportDto dto, User user) throws ErrorController.NotFoundException {
        Comment comment = commentRepository.findById(id).orElseThrow(ErrorController.NotFoundException::new);
        Report report = reportMapper.toReport(dto);
        report.createdBy = user;
        report.comment = comment;
        return reportRepository.save(report);
    }

    public Iterable<Report> findAll() {
        return reportRepository.findAll();
    }

    public Report addPostReport(Long id, Report.ReportDto dto, User user) throws ErrorController.NotFoundException {
        Post post = postRepository.findById(id).orElseThrow(ErrorController.NotFoundException::new);
        Report report = reportMapper.toReport(dto);
        report.createdBy = user;
        report.post = post;
        return reportRepository.save(report);
    }

    public Report solve(User user, Long id, Report.ReportResolution dto) throws ErrorController.NotFoundException {
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
