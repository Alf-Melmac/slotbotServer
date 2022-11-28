package de.webalf.slotbot.service.external;

import de.webalf.slotbot.constant.AuthorizationCheckValues;
import de.webalf.slotbot.repository.GlobalRoleRepository;
import de.webalf.slotbot.service.bot.BotService;
import de.webalf.slotbot.util.permissions.ApplicationPermissionHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static de.webalf.slotbot.util.permissions.PermissionHelper.buildGuildAuthentication;

/**
 * @author Alf
 * @since 19.09.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class DiscordAuthenticationService {
	private static final String ROLE_PREFIX = "Slotbot_";
	public static final String ROLE_ADMIN = ROLE_PREFIX + "Admin";
	public static final String ROLE_EVENT_MANGE = ROLE_PREFIX + "Event_Manage";
	public static final String ROLE_EVERYONE = "@everyone";
	public static final Set<String> KNOWN_ROLE_NAMES = Set.of(ROLE_ADMIN, ROLE_EVENT_MANGE, ROLE_EVERYONE);

	private final BotService botService;
	private final GlobalRoleRepository globalRoleRepository;

	public Set<String> getRoles(long userId) {
		Set<String> roles = new HashSet<>();

		botService.getJda().getGuildCache().stream().forEach(guild -> {
					Member guildMember;
					try {
						guildMember = guild.retrieveMemberById(userId).complete();
					} catch (ErrorResponseException e) {
						if (e.getErrorResponse() != ErrorResponse.UNKNOWN_MEMBER) {
							log.error("Error while retrieving user {} in guild {}", userId, guild.getIdLong(), e);
						}
						return;
					}
					Set<String> guildRoles = guildMember.getRoles().stream().map(Role::getName).collect(Collectors.toUnmodifiableSet());
					boolean noGuildRole = false;
					if (guildRoles.isEmpty()) {
						guildRoles = Set.of(ROLE_EVERYONE);
						noGuildRole = true;
					}
					final Set<String> memberRoles = guildRoles.stream()
							.filter(KNOWN_ROLE_NAMES::contains)
							.map(ApplicationPermissionHelper::getApplicationRoleName)
							.collect(Collectors.toUnmodifiableSet());
					roles.addAll(memberRoles);
					if (!noGuildRole) {
						roles.addAll(memberRoles.stream().map(role -> buildGuildAuthentication(role, guild.getIdLong())).collect(Collectors.toUnmodifiableSet()));
					}
				}
		);

		globalRoleRepository.findAllByUserId(userId).stream()
				.map(globalRole -> AuthorizationCheckValues.ROLE_PREFIX + globalRole.getRole().getApplicationRole())
				.forEachOrdered(roles::add);

		return roles;
	}
}
