package de.webalf.slotbot.repository;

import de.webalf.slotbot.model.BlogPost;
import de.webalf.slotbot.model.Guild;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Alf
 * @since 30.06.2024
 */
@Repository
public interface BlogPostRepository extends SuperIdEntityJpaRepository<BlogPost> {
	Page<BlogPost> findByGuildOrderByPinnedDescTimestampDesc(Guild guild, Pageable pageable);

	@Modifying
	@Query("UPDATE BlogPost b SET b.pinned = false WHERE b.guild = :guild")
	void updateAllPinnedToFalseByGuild(@Param("guild") Guild guild);
}
