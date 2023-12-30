package org.dbahrim.forum.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long id;

    public Date createdAt;

    @NotNull
    @NotBlank
    public String title;

    @NotNull
    @NotBlank
    public String content;

    @ManyToOne
    public User user;

    @ManyToOne
    @JoinColumn(name="category.id", nullable=false)
    public Category category;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(referencedColumnName = "id")
    public List<Comment> comments = Collections.emptyList();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    public Set<User> upvotedBy = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    public Set<User> dislikedBy = new HashSet<>();

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
        if (this.createdAt == null) {
            this.createdAt = new Date();
        }
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
