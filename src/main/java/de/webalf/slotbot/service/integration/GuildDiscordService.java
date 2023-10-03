package de.webalf.slotbot.service.integration;

import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.integration.DiscordCategory;
import de.webalf.slotbot.model.integration.DiscordRole;
import de.webalf.slotbot.model.integration.DiscordTextChannel;
import de.webalf.slotbot.service.bot.BotService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alf
 * @since 22.07.2023
 */
@Service
@RequiredArgsConstructor
public class GuildDiscordService {
	private final BotService botService;

	public boolean isConnected(long guildId) {
		return botService.getJda().getGuildById(guildId) != null;
	}

	public List<DiscordCategory> getGuildChannels(long guildId) {
		return getGuild(guildId).getCategoryCache().stream()
				.map(category -> DiscordCategory.builder()
						.name(category.getName())
						.textChannels(category.getTextChannels().stream()
								.map(textChannel -> DiscordTextChannel.builder()
										.id(textChannel.getId())
										.name(textChannel.getName())
										.build())
								.toList())
						.build())
				.toList();
	}

	public List<DiscordRole> getGuildRoles(long guildId) {
		return getGuild(guildId).getRoleCache().stream()
				.filter(role -> !role.isPublicRole())
				.map(role -> DiscordRole.builder()
						.id(role.getId())
						.name(role.getName())
						.build())
				.toList();
	}

	private Guild getGuild(long guildId) {
		final Guild guild = botService.getJda().getGuildById(guildId);
		if (guild == null) {
			throw BusinessRuntimeException.builder().title("Guild " + guildId + " couldn't be found.").build();
		}
		return guild;
	}
}
