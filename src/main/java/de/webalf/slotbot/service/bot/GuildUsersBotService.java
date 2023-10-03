package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.service.GuildUsersService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Wrapper for {@link GuildUsersService} to be used by discord bot
 *
 * @author Alf
 * @since 28.01.2023
 */
@Service
@RequiredArgsConstructor
public class GuildUsersBotService {
	private final GuildUsersService guildUsersService;

	public void add(long guildId, long userId) {
		guildUsersService.add(guildId, userId);
	}

	public void remove(long guildId, long userId) {
		guildUsersService.removeOptional(guildId, userId);
	}

	public void memberRolesAdd(long guildId, long userId, List<Role> addedRoles, List<Role> memberRoles) {
		guildUsersService.onRolesAdded(guildId, userId, getRoleIds(addedRoles), getRoleIds(memberRoles));
	}

	public void memberRolesRemove(long guildId, long userId, List<Role> removedRoles, List<Role> memberRoles) {
		guildUsersService.onRolesRemoved(guildId, userId, getRoleIds(removedRoles), getRoleIds(memberRoles));
	}

	private Set<Long> getRoleIds(List<Role> roles) {
		return roles.stream().map(Role::getIdLong).collect(Collectors.toUnmodifiableSet());
	}
}
