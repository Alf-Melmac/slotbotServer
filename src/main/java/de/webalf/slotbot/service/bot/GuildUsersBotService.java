package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.service.GuildUsersService;
import de.webalf.slotbot.service.SchedulerService;
import de.webalf.slotbot.util.bot.DiscordRoleUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Wrapper for {@link GuildUsersService} to be used by discord bot
 *
 * @author Alf
 * @since 28.01.2023
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GuildUsersBotService {
	private final GuildUsersService guildUsersService;
	private final SchedulerService schedulerService;

	private static final Map<GuildMember, RoleChange> SCHEDULED_ROLE_CHANGE = new ConcurrentHashMap<>();

	private record GuildMember(long guildId, long userId) {}

	private record RoleChange(Future<?> future, Set<Long> memberRoles) {}

	public void add(long discordGuildId, long userId) {
		guildUsersService.add(discordGuildId, userId);
	}

	public void remove(long discordGuildId, long userId) {
		guildUsersService.removeOptional(discordGuildId, userId);
	}

	@Async
	public void memberRolesAdd(long discordGuildId, long userId, List<Role> addedDiscordRoles, Set<Role> memberRoles) {
		scheduleRoleChange(discordGuildId, userId, addedDiscordRoles, memberRoles);
	}

	@Async
	public void memberRolesRemove(long discordGuildId, long userId, List<Role> removedDiscordRoles, Set<Role> memberRoles) {
		scheduleRoleChange(discordGuildId, userId, removedDiscordRoles, memberRoles);
	}

	private void scheduleRoleChange(long discordGuildId, long userId, List<Role> changedDiscordRoles, Set<Role> memberRoles) {
		final Set<Long> changedRoleIds = DiscordRoleUtils.getRoleIds(changedDiscordRoles);
		if (guildUsersService.noRoleConfiguredForGuild(discordGuildId, changedRoleIds)) {
			return;
		}

		final GuildMember guildMember = new GuildMember(discordGuildId, userId);
		final RoleChange roleChange = SCHEDULED_ROLE_CHANGE.get(guildMember);
		if (roleChange != null) {
			final Future<?> future = roleChange.future();
			if (future != null && !future.isDone()) {
				log.trace("Cancel scheduled role change for guild {} member {}", guildMember.guildId(), guildMember.userId());
				future.cancel(false);
			}
		}
		final Set<Long> memberRoleIds = DiscordRoleUtils.getRoleIds(memberRoles);
		SCHEDULED_ROLE_CHANGE.put(guildMember, new RoleChange(schedulerService.schedule(
				() -> guildUsersService.onRolesChanged(discordGuildId, userId, memberRoleIds),
				() -> SCHEDULED_ROLE_CHANGE.remove(guildMember),
				2, SECONDS),
				memberRoleIds));
	}
}
