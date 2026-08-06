package de.webalf.slotbot.service.integration;

import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.integration.DiscordCategory;
import de.webalf.slotbot.model.integration.DiscordRole;
import de.webalf.slotbot.model.integration.DiscordTextChannel;
import de.webalf.slotbot.service.bot.BotService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.stereotype.Service;

import java.util.List;

import static net.dv8tion.jda.api.Permission.MANAGE_ROLES;

/**
 * @author Alf
 * @since 22.07.2023
 */
@Service
@RequiredArgsConstructor
public class GuildDiscordService {
	private final BotService botService;

	public boolean isConnected(long discordGuildId) {
		return botService.getJda().getGuildById(discordGuildId) != null;
	}

	public boolean isAllowedToManageRoles(long discordGuildId) {
		return isAllowedToManageRoles(getGuildById(discordGuildId));
	}

	public boolean isAllowedToManageRoles(@NonNull Guild guild) {
		return guild.getSelfMember().hasPermission(MANAGE_ROLES);
	}

	public List<DiscordCategory> getGuildChannels(long discordGuildId) {
		return getGuildById(discordGuildId).getCategoryCache().stream()
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

	/**
	 * Return all {@link DiscordRole}s the bot {@link Member#canInteract(Role) can interact} with
	 *
	 * @param discordGuildId to get roles for
	 * @return all roles that can be interacted with
	 */
	public List<DiscordRole> getGuildRoles(long discordGuildId) {
		final Guild guild = getGuildById(discordGuildId);
		final Member selfMember = guild.getSelfMember();
		return guild.getRoleCache().stream()
				.filter(role -> !role.isPublicRole() && selfMember.canInteract(role))
				.map(role -> DiscordRole.builder()
						.id(role.getId())
						.name(role.getName())
						.build())
				.toList();
	}

	/**
	 * Returns the {@link Guild} associated with the given id
	 *
	 * @param discordGuildId to find guild for
	 * @return Guild found by id
	 * @throws BusinessRuntimeException if no guild with this id could be found
	 */
	public Guild getGuildById(long discordGuildId) {
		final Guild guild = botService.getJda().getGuildById(discordGuildId);
		if (guild == null) {
			throw BusinessRuntimeException.builder().title("Guild " + discordGuildId + " couldn't be found.").build();
		}
		return guild;
	}

	public void leaveGuild(long discordGuildId) {
		getGuildById(discordGuildId)
				.leave()
				.queue();
	}
}
