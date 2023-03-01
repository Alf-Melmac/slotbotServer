package de.webalf.slotbot.assembler.website;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.website.UserInGuildDto;
import de.webalf.slotbot.model.external.discord.DiscordGuildMember;
import de.webalf.slotbot.service.external.DiscordBotService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 18.01.2023
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserInGuildAssembler {
	private final DiscordBotService discordBotService;

	public UserInGuildDto toDto(@NonNull User user, @NonNull Guild guild) {
		final DiscordGuildMember member = discordBotService.getGuildMember(user.getId(), guild.getId());
		if (member == null) return null;

		return UserInGuildDto.builder()
				.user(DiscordUserAssembler.toDto(member))
				.build();
	}

	public List<UserInGuildDto> toDtoList(@NonNull Iterable<? extends User> users, @NonNull Guild guild) {
		return StreamSupport.stream(users.spliterator(), false)
				.map(user -> this.toDto(user, guild))
				.toList();
	}
}
