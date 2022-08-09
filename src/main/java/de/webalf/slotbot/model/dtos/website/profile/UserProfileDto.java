package de.webalf.slotbot.model.dtos.website.profile;

import de.webalf.slotbot.model.dtos.website.DiscordUserDto;
import lombok.Builder;
import lombok.Value;

/**
 * @author Alf
 * @since 03.08.2022
 */
@Value
@Builder
public class UserProfileDto {
	DiscordUserDto user;
	String roles;
	long participatedEventsCount;

	boolean ownProfile;
}
