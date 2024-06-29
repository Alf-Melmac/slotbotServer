package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.BlogPost;
import org.springframework.stereotype.Repository;

/**
 * @author Alf
 * @since 30.06.2024
 */
@Repository
public interface BlogPostRepository extends SuperIdEntityJpaRepository<BlogPost> {
}
