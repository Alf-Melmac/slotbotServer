package de.webalf.slotbot.util.bot;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 19.08.2025
 */
@UtilityClass
public final class DiscordRoleUtils {
	/**
	 * Returns the {@link Role} with the given id in the given guild
	 *
	 * @param guild  to get role from
	 * @param roleId id of the role to get
	 * @return role with the given id
	 * @throws IllegalStateException if no role with the given id could be found
	 */
	public static Role getRoleById(@NonNull Guild guild, Long roleId) {
		if (roleId == null) {
			return null;
		}
		final Role role = guild.getRoleById(roleId);
		if (role == null) {
			throw new IllegalStateException("Role " + roleId + " couldn't be found.");
		}
		return role;
	}

	/**
	 * Extracts the ids of the given roles
	 */
	public static Set<Long> getRoleIds(Iterable<Role> roles) {
		return StreamSupport.stream(roles.spliterator(), false)
				.map(Role::getIdLong)
				.collect(Collectors.toUnmodifiableSet());
	}
}
