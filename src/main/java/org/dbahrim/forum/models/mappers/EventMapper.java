package org.dbahrim.forum.models.mappers;

import org.dbahrim.forum.models.Event;
import org.dbahrim.forum.models.dtos.EventPost;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event sourceToDestination(EventPost source);
}
