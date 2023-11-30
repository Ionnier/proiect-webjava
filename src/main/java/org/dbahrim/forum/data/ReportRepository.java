package org.dbahrim.forum.data;

import org.dbahrim.forum.models.Report;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReportRepository extends CrudRepository<Report, Long> {
    List<Report> findByMessage(String message);
}
