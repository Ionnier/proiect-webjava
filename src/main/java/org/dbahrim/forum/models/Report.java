package org.dbahrim.forum.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Entity()
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
public class Report implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    public Long id;

    private Date createdAt;
    private String message;
    private Resolution resolution = Resolution.NOT_VERIFIED;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    public User createdBy;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    public User resolvedBy;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    public Comment comment;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
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
}
