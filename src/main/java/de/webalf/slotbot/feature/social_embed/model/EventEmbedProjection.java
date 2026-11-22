package de.webalf.slotbot.feature.social_embed.model;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.Guild;

/**
 * Subset of {@link Event} for its link preview
 *
 * @author Alf
 * @since 22.11.2026
 */
public interface EventEmbedProjection {
	long getId();

	String getName();

	String getDescription();

	String getPictureUrl();

	EventType getEventType();

	Guild getOwnerGuild();
}
