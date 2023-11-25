package org.dbahrim.forum.models.mappers;

import org.dbahrim.forum.models.Report;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    Report toReport(Report.ReportDto dto);
}
