package org.dbahrim.forum.data;

import io.swagger.v3.oas.annotations.Hidden;
import org.dbahrim.forum.models.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Hidden
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByMessage(String message);
}
