package org.dbahrim.forum.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.assertj.core.util.VisibleForTesting;
import org.dbahrim.forum.controllers.ErrorController;

import java.util.List;

@Entity
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
@Data
public class Event {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    public Long id;

    @NotNull
    @NotBlank
    @Size(min = 5)
    public String title;

    @NotNull
    public Long timestamp;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    public User createdBy;

    @PrePersist
    @VisibleForTesting
    public void preCreate() throws ErrorController.BadRequest {
        if (createdBy == null) {
            throw new ErrorController.BadRequest();
        }
    }

}
