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
public class GuildUserEventListener {
	private final GuildService guildService;
	private final GuildDiscordService guildDiscordService;

	@EventListener
	@Async
	public void onGuildUserCreatedEvent(@NonNull GuildUserCreatedEvent event) {
		final long guildId = event.guildId();
		final Guild guild = guildService.find(guildId);

		final Long discordRoleId = guild.getDiscordRole(event.role());
		if (discordRoleId == null) {
			return;
		}
		final net.dv8tion.jda.api.entities.Guild discordGuild = guildDiscordService.getGuildById(guildId);
		log.trace("Trying to add role {} to user {} in guild {}", discordRoleId, event.userId(), guildId);
		final Role discordRole = DiscordRoleUtils.getRoleById(discordGuild, discordRoleId);
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
		if (Objects.equals(oldDiscordRoleId, newDiscordRoleId)) {
			return;
		}
		final net.dv8tion.jda.api.entities.Guild discordGuild = guildDiscordService.getGuildById(guildId);
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
		final Guild guild = guildService.find(guildId);

		final Long discordRoleId = guild.getDiscordRole(event.role());
		if (discordRoleId == null) {
			return;
		}
		final net.dv8tion.jda.api.entities.Guild discordGuild = guildDiscordService.getGuildById(guildId);
		final Role discordRole = DiscordRoleUtils.getRoleById(discordGuild, discordRoleId);
		log.trace("Removing role {} from user {} in guild {}", discordRole, event.userId(), guildId);
		discordGuild.removeRoleFromMember(getMemberById(discordGuild, event.userId()), discordRole).queue();
	}

	private Member getMemberById(@NonNull net.dv8tion.jda.api.entities.Guild discordGuild, long userId) {
		final Member member = discordGuild.getMemberById(userId);
		if (member == null) {
			throw new IllegalStateException("Member " + userId + " couldn't be found.");
		}
		return member;
	}
}
