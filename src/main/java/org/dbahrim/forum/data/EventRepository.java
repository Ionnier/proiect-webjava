package org.dbahrim.forum.data;

import io.swagger.v3.oas.annotations.Hidden;
import org.dbahrim.forum.models.Comment;
import org.dbahrim.forum.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Hidden
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByTitleAndTimestampBetween(String title, Long start, Long end);
}
