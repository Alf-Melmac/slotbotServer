package de.webalf.slotbot.feature.social_embed.model;

import lombok.NonNull;
import org.apache.commons.lang3.EnumUtils;

import java.util.Optional;

/**
 * Frontend pages that get page specific social embed tags
 *
 * @author Alf
 * @since 07.11.2026
 */
public enum SocialEmbedType {
	EVENTS,
	GUILDS,
	PROFILE;

	public static Optional<SocialEmbedType> fromPath(@NonNull String path) {
		return Optional.ofNullable(EnumUtils.getEnum(SocialEmbedType.class, path.toUpperCase()));
	}
}
