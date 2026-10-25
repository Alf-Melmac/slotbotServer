package de.webalf.slotbot.service.bot.listener;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.event.GuildUserCreatedEvent;
import de.webalf.slotbot.model.event.GuildUserDeleteEvent;
import de.webalf.slotbot.model.event.GuildUserRoleUpdateEvent;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.service.integration.GuildDiscordService;
import de.webalf.slotbot.util.bot.DiscordRoleUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * @author Alf
 * @since 03.10.2023
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GuildUserBotListener {
	private final GuildService guildService;
	private final GuildDiscordService guildDiscordService;

	@EventListener
	@Async
	public void onGuildUserCreatedEvent(@NonNull GuildUserCreatedEvent event) {
		final long guildId = event.guildId();
		final DiscordGuildRole guildRole = findDiscordGuildRole(guildId, event.role());
		if (guildRole == null) {
			return;
		}
		final net.dv8tion.jda.api.entities.Guild discordGuild = guildRole.guild();
		log.trace("Trying to add role {} to user {} in guild {}", guildRole.roleId(), event.userId(), guildId);
		final Role discordRole = DiscordRoleUtils.getRoleById(discordGuild, guildRole.roleId());
		log.trace("Adding role {} to user {} in guild {}", discordRole, event.userId(), guildId);
		discordGuild.addRoleToMember(getMemberById(discordGuild, event.userId()), discordRole).queue();
	}

	@EventListener
	@Async
	public void onGuildUserRoleUpdateEvent(@NonNull GuildUserRoleUpdateEvent event) {
		final long guildId = event.guildId();
		final Guild guild = guildService.findExisting(guildId);

		final Long discordGuildId = guild.getDiscordId();
		if (discordGuildId == null) {
			return;
		}
		final Long oldDiscordRoleId = guild.getDiscordRole(event.oldRole());
		final Long newDiscordRoleId = guild.getDiscordRole(event.newRole());
		if (Objects.equals(oldDiscordRoleId, newDiscordRoleId)) {
			return;
		}
		final net.dv8tion.jda.api.entities.Guild discordGuild = guildDiscordService.getGuildById(discordGuildId);
		if (!guildDiscordService.isAllowedToManageRoles(discordGuild)) {
			log.debug("Bot is not allowed to manage roles in guild {}", guildId);
			return;
		}
		final Role oldDiscordRole = DiscordRoleUtils.getRoleById(discordGuild, oldDiscordRoleId);
		final Role newDiscordRole = DiscordRoleUtils.getRoleById(discordGuild, newDiscordRoleId);
		log.trace("Updating roles for user {} in guild {}. Old role {}, new role {}", event.userId(), guildId, oldDiscordRole, newDiscordRole);
		discordGuild.modifyMemberRoles(
						getMemberById(discordGuild, event.userId()),
						newDiscordRole != null ? Set.of(newDiscordRole) : Collections.emptySet(),
						oldDiscordRole != null ? Set.of(oldDiscordRole) : Collections.emptySet())
				.queue();
	}

	@EventListener
	@Async
	public void onGuildUserDeleteEvent(@NonNull GuildUserDeleteEvent event) {
		final long guildId = event.guildId();
		final DiscordGuildRole guildRole = findDiscordGuildRole(guildId, event.role());
		if (guildRole == null) {
			return;
		}
		final net.dv8tion.jda.api.entities.Guild discordGuild = guildRole.guild();
		final Member member = discordGuild.getMemberById(event.userId());
		if (member == null) {
			log.trace("User {} is no longer a member of guild {}, no role to remove", event.userId(), guildId);
			return;
		}
		final Role discordRole = DiscordRoleUtils.getRoleById(discordGuild, guildRole.roleId());
		log.trace("Removing role {} from user {} in guild {}", discordRole, event.userId(), guildId);
		discordGuild.removeRoleFromMember(member, discordRole).queue();
	}

	private record DiscordGuildRole(net.dv8tion.jda.api.entities.Guild guild, long roleId) {}

	/**
	 * Finds the discord guild and the discord role configured for the given role
	 *
	 * @param guildId guild to look up
	 * @param role    to find discord role for
	 * @return discord guild with role id, or {@code null} if the guild has no discord guild or no discord role is configured
	 */
	private DiscordGuildRole findDiscordGuildRole(long guildId, de.webalf.slotbot.util.permissions.Role role) {
		final Guild guild = guildService.findExisting(guildId);

		final Long discordGuildId = guild.getDiscordId();
		if (discordGuildId == null) {
			return null;
		}
		final Long discordRoleId = guild.getDiscordRole(role);
		if (discordRoleId == null) {
			return null;
		}
		return new DiscordGuildRole(guildDiscordService.getGuildById(discordGuildId), discordRoleId);
	}

	private Member getMemberById(@NonNull net.dv8tion.jda.api.entities.Guild discordGuild, long userId) {
		final Member member = discordGuild.getMemberById(userId);
		if (member == null) {
			throw new IllegalStateException("Member " + userId + " couldn't be found.");
		}
		return member;
	}
}
