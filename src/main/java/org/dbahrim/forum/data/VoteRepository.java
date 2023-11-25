package org.dbahrim.forum.data;

import org.dbahrim.forum.models.Vote;
import org.springframework.data.repository.CrudRepository;

public interface VoteRepository extends CrudRepository<Vote, Long> {
}
