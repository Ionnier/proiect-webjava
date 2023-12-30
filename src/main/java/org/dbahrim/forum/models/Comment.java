package org.dbahrim.forum.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor(force=true)
public class Comment implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private final Long id;

    public Date createdAt;

    @NotBlank
    public String content;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false, updatable = false)
    private User user;

    public Long postId;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    public Set<User> upvotedBy = Collections.emptySet();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    public Set<User> dislikedBy = Collections.emptySet();

    @PrePersist
    void prePersist() {
        this.createdAt = new Date();
    }

    @Data
    @NoArgsConstructor(access= AccessLevel.PUBLIC, force=true)
    public static class CommentPost {
        public final String content;
    }
}


