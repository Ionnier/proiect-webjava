package org.dbahrim.forum.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EventPost {
    @NotNull
    @NotBlank
    public String title;

    @NotNull
    public Long timestamp;
}
