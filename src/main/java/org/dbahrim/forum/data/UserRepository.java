package org.dbahrim.forum.data;

import io.swagger.v3.oas.annotations.Hidden;
import org.dbahrim.forum.models.User;
import org.springframework.data.repository.CrudRepository;

@Hidden
public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);
}
