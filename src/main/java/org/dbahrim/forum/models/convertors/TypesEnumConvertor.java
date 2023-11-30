package org.dbahrim.forum.models.convertors;

import org.dbahrim.forum.controllers.VoteController;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TypesEnumConvertor implements Converter<String, VoteController.Types> {
    @Override
    public VoteController.Types convert(String type) {
        if (type.trim().equalsIgnoreCase(VoteController.Types.POST.name())) {
            return VoteController.Types.POST;
        } else if (type.trim().equalsIgnoreCase(VoteController.Types.COMMENT.name())) {
            return VoteController.Types.COMMENT;
        }
        return null;
    }
}