package org.dbahrim.forum.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Entity()
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
public class Report implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    private Date createdAt;
    public String message;
    public Resolution resolution = Resolution.NOT_VERIFIED;

    @ManyToOne
    public User createdBy;

    @ManyToOne
    public User resolvedBy;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    public Comment comment;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    public Post post;

    @PrePersist
    void prePersist() {
        this.createdAt = new Date();
    }

    public enum Resolution {
        NOT_VERIFIED, DELETED, CLEANED, NOT_VALID
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
    public static class ReportDto {
        @NotBlank(message = "Message should not be empty")
        public String message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
    public static class ReportResolution {
        @NotBlank(message = "Message should not be empty")
        public String message;

        @NotNull
        public Resolution resolution;
    }
}
