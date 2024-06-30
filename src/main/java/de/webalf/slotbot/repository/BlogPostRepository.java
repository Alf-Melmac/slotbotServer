package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.BlogPost;
import de.webalf.slotbot.model.Guild;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alf
 * @since 30.06.2024
 */
@Repository
public interface BlogPostRepository extends SuperIdEntityJpaRepository<BlogPost> {
	List<BlogPost> findByGuildOrderByPinnedDescTimestampDesc(Guild guild);
}
