package de.webalf.slotbot.model.event;

import de.webalf.slotbot.model.EventDiscordInformation;

/**
 * User is interested in an event for which they haven't slotted yet
 *
 * @author Alf
 * @since 23.05.26
 */
public record InterestedWithoutSlotEvent(
		EventDiscordInformation discordInformation,
		long userId
) {}
