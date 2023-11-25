package org.dbahrim.forum.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

@Entity
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Post implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private Date createdAt;

    public String title;

    public String content;

    @ManyToOne
    public User user;

    @ManyToOne
    @JoinColumn(name="category.id", nullable=false)
    public Category category;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public List<Comment> comments = Collections.emptyList();

    public Post(Category category, User user, String content, String title) {
        this.category = category;
        this.user = user;
        this.content = content;
        this.title = title;
    }

    public Optional<Post> fromPatch(Post.PostPutPatchRequest patch, Category newCategory, Boolean isPatch) {
        if (!Objects.equals(this.id, patch.getId())) {
            return Optional.empty();
        }
        if (!isPatch && patch.content == null && newCategory == null && patch.title == null) {
            return Optional.empty();
        }

        if (patch.content != null) {
            this.content = patch.content;
        }

        if (patch.title != null) {
            this.title = patch.title;
        }

        if (newCategory != null) {
            this.category = newCategory;
        }
        return Optional.of(this);
    }

    public boolean addComment(Comment comment) {
        return comments.add(comment);
    }

    @OneToMany
    private List<Report> reportList;

    @PrePersist
    void prePersist() {
        this.createdAt = new Date();
    }

    @Data
    @AllArgsConstructor
    public static class PostPostRequest {
        @NotNull
        public Long categoryId;

        @NotBlank(message="Title is required")
        @Size(min=25, message="Title must be at least 25 characters long")
        public String title;

        @NotBlank(message="Content is required")
        @Size(min=100, message="Content must be at least 100 characters long")
        public String content;
    }

    @Data
    @AllArgsConstructor
    public static class PostPutPatchRequest {
        public Long categoryId;
        public Long id;
        public String title;
        public String content;
    }
}
