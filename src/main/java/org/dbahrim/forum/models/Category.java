package org.dbahrim.forum.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
@NoArgsConstructor(access= AccessLevel.PUBLIC, force=true)
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @NotBlank(message="Name is required")
    @Size(min=5, message="Name must be at least 5 characters long")
    private String name;

    @NotBlank(message="Description is required")
    @Size(min=30, message="Description must be at least 30 characters long")
    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category", fetch = FetchType.LAZY)
    private final List<Post> postList;

    public Category(CategoryRequestBodyPost post) {
        name = post.name;
        description = post.description;
        id = null;
        postList = null;
    }

    public Optional<Category> fromPatch(CategoryRequestBodyPutPatch patch, Boolean isPatch) {
        if (!Objects.equals(this.id, patch.getId())) {
            return Optional.empty();
        }
        if (!isPatch && patch.description == null && patch.name == null) {
            return Optional.empty();
        }
        if (patch.description != null) {
            this.description = patch.description;
        }
        if (patch.name != null) {
            this.name = patch.name;
        }
        return Optional.of(this);
    }

    @Data
    @AllArgsConstructor
    public static class CategoryRequestBodyPost {
        @NotBlank(message="Name is required")
        @Size(min=5, message="Name must be at least 5 characters long")
        private final String name;

        @NotBlank(message="Description is required")
        @Size(min=30, message="Description must be at least 30 characters long")
        private final String description;
    }

    @Data
    @AllArgsConstructor
    public static class CategoryRequestBodyPutPatch {
        @NotNull
        private final Long id;

        @NotBlank(message="Name is required")
        @Size(min=5, message="Name must be at least 5 characters long")
        private final String name;

        @NotBlank(message="Description is required")
        @Size(min=30, message="Description must be at least 30 characters long")
        private final String description;
    }
}
