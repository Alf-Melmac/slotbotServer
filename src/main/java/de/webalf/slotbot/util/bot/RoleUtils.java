package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.service.external.DiscordAuthenticationService;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Alf
 * @since 03.08.2021
 */
@UtilityClass
@Slf4j
public final class RoleUtils {
	/**
	 * Checks for the given guild if every required role is created. If not, they will be created
	 *
	 * @param guild to check roles for
	 */
	public static void checkRequiredRoles(@NonNull Guild guild) {
		final Set<String> knownRoleNames = DiscordAuthenticationService.KNOWN_ROLE_NAMES;
		final Set<String> existingRoles = guild.getRoles().stream().map(Role::getName).collect(Collectors.toUnmodifiableSet());

		knownRoleNames.stream().filter(roleName -> !existingRoles.contains(roleName))
				.forEach(roleName -> createRole(roleName, guild));
	}

	private static void createRole(String roleName, @NonNull Guild guild) {
		log.warn("Found missing role {}. Creating...", roleName);
		//Later services require this role therefore this request must be blocking
		guild.createRole().setName(roleName).setPermissions(Permission.EMPTY_PERMISSIONS).complete();
		log.warn("Created role {}", roleName);
	}
}
