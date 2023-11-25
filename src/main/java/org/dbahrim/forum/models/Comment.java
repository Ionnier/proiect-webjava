package org.dbahrim.forum.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor(force=true)
public class Comment implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private final Long id;

    private Date createdAt;

    @NotBlank
    public String content;

    @ManyToOne
    private User user;

    @PrePersist
    void prePersist() {
        this.createdAt = new Date();
    }

    @Data
    @NoArgsConstructor(access= AccessLevel.PRIVATE, force=true)
    public class CommentPost {
        public final String content;
    }
}


