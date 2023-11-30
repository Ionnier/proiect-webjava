package org.dbahrim.forum.models.convertors;

import org.dbahrim.forum.controllers.ErrorController;
import org.dbahrim.forum.controllers.VoteController;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class WayEnumConvertor implements Converter<String, VoteController.Way> {
    @Override
    public VoteController.Way convert(String way) {
        if (way.trim().equalsIgnoreCase(VoteController.Way.UP.name())) {
            return VoteController.Way.UP;
        } else if (way.trim().equalsIgnoreCase(VoteController.Way.DOWN.name())) {
            return VoteController.Way.DOWN;
        } else if (way.trim().equalsIgnoreCase(VoteController.Way.CANCEL.name())) {
            return VoteController.Way.CANCEL;
        }
        return null;
    }
}
