package org.dbahrim.forum.data;

import io.swagger.v3.oas.annotations.Hidden;
import org.dbahrim.forum.models.Report;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Hidden
public interface ReportRepository extends CrudRepository<Report, Long> {
    List<Report> findByMessage(String message);
}
