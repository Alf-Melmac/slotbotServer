package de.webalf.slotbot.service.bot.listener;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.event.GuildUserCreatedEvent;
import de.webalf.slotbot.model.event.GuildUserDeleteEvent;
import de.webalf.slotbot.model.event.GuildUserRoleUpdateEvent;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.service.bot.BotService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

/**
 * @author Alf
 * @since 03.10.2023
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GuildUserEventListener {
	private final GuildService guildService;
	private final BotService botService;

	@EventListener
	@Async
	public void onGuildUserCreatedEvent(@NonNull GuildUserCreatedEvent event) {
		final long guildId = event.guildId();
		final Guild guild = guildService.find(guildId);

		final Long discordRoleId = guild.getDiscordRole(event.role());
		if (discordRoleId == null) {
			return;
		}
		final net.dv8tion.jda.api.entities.Guild discordGuild = getGuildById(guildId);
		final Role discordRole = getRoleById(discordGuild, discordRoleId);
		log.trace("Adding role {} to user {} in guild {}", discordRole, event.userId(), guildId);
		discordGuild.addRoleToMember(getMemberById(discordGuild, event.userId()), discordRole).queue();
	}

	@EventListener
	@Async
	public void onGuildUserRoleUpdateEvent(@NonNull GuildUserRoleUpdateEvent event) {
		final long guildId = event.guildId();
		final Guild guild = guildService.find(guildId);

		final Long oldDiscordRoleId = guild.getDiscordRole(event.oldRole());
		final Long newDiscordRoleId = guild.getDiscordRole(event.newRole());
		if (oldDiscordRoleId == null && newDiscordRoleId == null) {
			return;
		}
		final net.dv8tion.jda.api.entities.Guild discordGuild = getGuildById(guildId);
		final Role oldDiscordRole = getRoleById(discordGuild, oldDiscordRoleId);
		final Role newDiscordRole = getRoleById(discordGuild, newDiscordRoleId);
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
		final Guild guild = guildService.find(guildId);

		final Long discordRoleId = guild.getDiscordRole(event.role());
		if (discordRoleId == null) {
			return;
		}
		final net.dv8tion.jda.api.entities.Guild discordGuild = getGuildById(guildId);
		final Role discordRole = getRoleById(discordGuild, discordRoleId);
		log.trace("Removing role {} from user {} in guild {}", discordRole, event.userId(), guildId);
		discordGuild.removeRoleFromMember(getMemberById(discordGuild, event.userId()), discordRole).queue();
	}

	private static final String NOT_FOUND = " couldn't be found.";

	private net.dv8tion.jda.api.entities.Guild getGuildById(long guildId) {
		final net.dv8tion.jda.api.entities.Guild guild = botService.getJda().getGuildById(guildId);
		if (guild == null) {
			throw new IllegalStateException("Guild " + guildId + NOT_FOUND);
		}
		return guild;
	}

	private Member getMemberById(@NonNull net.dv8tion.jda.api.entities.Guild discordGuild, long userId) {
		final Member member = discordGuild.getMemberById(userId);
		if (member == null) {
			throw new IllegalStateException("Member " + userId + NOT_FOUND);
		}
		return member;
	}

	private Role getRoleById(@NonNull net.dv8tion.jda.api.entities.Guild discordGuild, Long roleId) {
		if (roleId == null) {
			return null;
		}
		final Role role = discordGuild.getRoleById(roleId);
		if (role == null) {
			throw new IllegalStateException("Role " + roleId + NOT_FOUND);
		}
		return role;
	}
}
