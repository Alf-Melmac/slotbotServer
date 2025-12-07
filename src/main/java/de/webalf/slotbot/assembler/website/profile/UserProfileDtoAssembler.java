package de.webalf.slotbot.assembler.website.profile;

import de.webalf.slotbot.assembler.website.DiscordUserAssembler;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.website.profile.LastEventInfo;
import de.webalf.slotbot.model.dtos.website.profile.UserProfileDto;
import de.webalf.slotbot.model.external.discord.DiscordUser;
import de.webalf.slotbot.service.EventService;
import de.webalf.slotbot.service.GuildUsersService;
import de.webalf.slotbot.service.SlotService;
import de.webalf.slotbot.service.UserUpdateService;
import de.webalf.slotbot.service.external.DiscordBotService;
import de.webalf.slotbot.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

import static de.webalf.slotbot.util.permissions.PermissionHelper.isLoggedInUser;

/**
 * @author Alf
 * @since 14.04.2024
 */
@Component
@RequiredArgsConstructor
public class UserProfileDtoAssembler {
	private final DiscordBotService discordBotService;
	private final GuildUsersService guildUsersService;
	private final UserUpdateService userService;
	private final SlotService slotService;
	private final EventService eventService;

	public UserProfileDto toDto(long userId) {
		final DiscordUser discordUser = discordBotService.getUser(userId);
		if (discordUser == null) {
			throw new ResourceNotFoundException("Unknown discord user " + userId);
		}

		final User user = userService.find(userId);
		final boolean ownProfile = isLoggedInUser(userId);

		final Optional<Event> lastEvent = eventService.findLastEvent(user);
		LastEventInfo lastEventInfo = null;
		if (lastEvent.isPresent()) {
			final Event event = lastEvent.get();
			lastEventInfo = LastEventInfo.builder()
					.id(event.getId())
					.daysSince(Duration.between(event.getDateTime().toLocalDate().atStartOfDay(), DateUtils.now().toLocalDate().atStartOfDay()).toDays())
					.build();
		}
		return UserProfileDto.builder()
				.user(DiscordUserAssembler.toDto(discordUser))
				.roles(String.join(", ", guildUsersService.getApplicationRoles(userId)))
				.participatedEventsCount(slotService.countByUserBeforeToday(user))
				.lastEvent(lastEventInfo)
				.ownProfile(ownProfile)
				.build();
	}
}
