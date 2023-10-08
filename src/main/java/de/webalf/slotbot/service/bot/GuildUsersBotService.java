package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.service.GuildUsersService;
import de.webalf.slotbot.service.SchedulerService;
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
import java.util.stream.Collectors;

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

	public void add(long guildId, long userId) {
		guildUsersService.add(guildId, userId);
	}

	public void remove(long guildId, long userId) {
		guildUsersService.removeOptional(guildId, userId);
	}

	@Async
	public void memberRolesAdd(long guildId, long userId, List<Role> addedDiscordRoles, List<Role> memberRoles) {
		scheduleRoleChange(guildId, userId, addedDiscordRoles, memberRoles);
	}

	@Async
	public void memberRolesRemove(long guildId, long userId, List<Role> removedDiscordRoles, List<Role> memberRoles) {
		scheduleRoleChange(guildId, userId, removedDiscordRoles, memberRoles);
	}

	private void scheduleRoleChange(long guildId, long userId, List<Role> changedDiscordRoles, List<Role> memberRoles) {
		final Set<Long> changedRoleIds = getRoleIds(changedDiscordRoles);
		if (guildUsersService.noRoleConfiguredForGuild(guildId, changedRoleIds)) {
			return;
		}

		final GuildMember guildMember = new GuildMember(guildId, userId);
		final RoleChange roleChange = SCHEDULED_ROLE_CHANGE.get(guildMember);
		if (roleChange != null) {
			final Future<?> future = roleChange.future();
			if (future != null && !future.isDone()) {
				log.trace("Cancel scheduled role change for guild {} member {}", guildMember.guildId(), guildMember.userId());
				future.cancel(false);
			}
		}
		final Set<Long> memberRoleIds = getRoleIds(memberRoles);
		SCHEDULED_ROLE_CHANGE.put(guildMember, new RoleChange(schedulerService.schedule(
				() -> guildUsersService.onRolesChanged(guildId, userId, memberRoleIds),
				() -> SCHEDULED_ROLE_CHANGE.remove(guildMember),
				2, SECONDS),
				memberRoleIds));
	}

	private Set<Long> getRoleIds(List<Role> roles) {
		return roles.stream().map(Role::getIdLong).collect(Collectors.toUnmodifiableSet());
	}
}
