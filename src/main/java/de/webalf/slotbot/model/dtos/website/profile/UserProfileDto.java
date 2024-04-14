package de.webalf.slotbot.model.dtos.website.profile;

import de.webalf.slotbot.model.dtos.website.DiscordUserDto;
import lombok.Builder;

/**
 * @author Alf
 * @since 03.08.2022
 */
@Builder
public record UserProfileDto(
		DiscordUserDto user,
		String roles,
		long participatedEventsCount,
		LastEventInfo lastEvent,
		boolean ownProfile
) {}
