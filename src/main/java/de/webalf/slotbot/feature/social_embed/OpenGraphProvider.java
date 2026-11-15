package de.webalf.slotbot.feature.social_embed;

import de.webalf.slotbot.feature.social_embed.model.OpenGraph;
import de.webalf.slotbot.feature.social_embed.model.SocialEmbedType;

import java.util.Optional;

/**
 * Resolves the {@link OpenGraph} for one page
 *
 * @author Alf
 * @since 02.11.2026
 */
public interface OpenGraphProvider {
	/**
	 * @return page this provider is responsible for
	 */
	SocialEmbedType type();

	/**
	 * @param id of the requested entity
	 * @return meta information for the page or empty if no information is available
	 */
	Optional<OpenGraph> resolve(long id);
}
