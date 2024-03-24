package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.GuildUser;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.event.GuildUserCreatedEvent;
import de.webalf.slotbot.model.event.GuildUserDeleteEvent;
import de.webalf.slotbot.model.event.GuildUserRoleUpdateEvent;
import de.webalf.slotbot.repository.GlobalRoleRepository;
import de.webalf.slotbot.repository.GuildUsersRepository;
import de.webalf.slotbot.util.permissions.Role;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static de.webalf.slotbot.util.permissions.PermissionHelper.buildAuthenticationWithPrefix;
import static de.webalf.slotbot.util.permissions.PermissionHelper.buildGuildAuthenticationWithPrefix;

/**
 * @author Alf
 * @since 18.01.2023
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class GuildUsersService {
	private final GuildUsersRepository guildUsersRepository;
	private final UserService userService;
	private final GuildService guildService;
	private final GlobalRoleRepository globalRoleRepository;
	private final SessionRegistry sessionRegistry;
	private final ApplicationEventPublisher eventPublisher;

	public Page<GuildUser> findGuildUsers(Guild guild, Pageable pageable) {
		return guildUsersRepository.findByGuild(guild, pageable);
	}

	public Set<GuildUser> findByUserId(long userId) {
		return guildUsersRepository.findByIdUserId(userId);
	}

	public GuildUser add(long guildId, long userId) {
		final Guild guild = guildService.find(guildId);
		final User user = userService.find(userId);

		log.trace("Adding user {} to guild {}", userId, guildId);
		return guildUsersRepository.findByGuildAndUser(guild, user)
				.orElseGet(() -> create(guild, user, null));
	}

	private GuildUser create(@NonNull Guild guild, @NonNull User user, Role role) {
		final GuildUser guildUser = guildUsersRepository.save(GuildUser.builder().user(user).guild(guild).role(role).build());
		eventPublisher.publishEvent(new GuildUserCreatedEvent(guildUser));
		return guildUser;
	}

	public void remove(long guildId, long userId) {
		final User user = userService.findExisting(userId);
		final Guild guild = guildService.findExisting(guildId);

		log.trace("Removing user {} from guild {}", userId, guildId);
		guildUsersRepository.findByGuildAndUser(guild, user)
				.ifPresent(guildUser -> {
					eventPublisher.publishEvent(new GuildUserDeleteEvent(guildUser));
					guildUsersRepository.delete(guildUser);
				});
	}

	@Async
	public void removeOptional(long guildId, long userId) {
		invalidateSession(userId);
		guildUsersRepository.deleteById_GuildIdAndId_UserId(guildId, userId);
	}

	/**
	 * Sets the given role for the given user in the given guild
	 *
	 * @param guildId guild to set role for
	 * @param userId  user with role
	 * @param role    to set
	 */
	public void setRole(long guildId, long userId, Role role) {
		final Guild guild = guildService.find(guildId);
		final User user = userService.find(userId);

		log.trace("Setting role {} for user {} in guild {}", role, userId, guildId);
		guildUsersRepository.findByGuildAndUser(guild, user)
				.ifPresentOrElse(guildUser -> guildUser.setRole(role),
						() -> create(guild, user, role));
	}

	/**
	 * Checks if one of the given roles is configured for any role in the given guild
	 *
	 * @return true if no role matches a configured role
	 * @see GuildService#existsByIdAndAnyRoleIn(long, Set)
	 */
	public boolean noRoleConfiguredForGuild(long guildId, Set<Long> roles) {
		return !guildService.existsByIdAndAnyRoleIn(guildId, roles);
	}

	/**
	 * Processes changes in the roles of the given user in the given guild.
	 *
	 * @param guildId     guild the roles changed in
	 * @param userId      user whose roles changed
	 * @param memberRoles new set of roles of the user
	 */
	public void onRolesChanged(long guildId, long userId, Set<Long> memberRoles) {
		final Guild guild = guildService.findExisting(guildId);
		final Role newRole = evaluateRole(guild, memberRoles);
		//If a member role is configured and no role is left, remove user from guild
		if (guild.getMemberRole() != null && !memberRoles.contains(guild.getMemberRole()) && newRole == null) {
			remove(guildId, userId);
			return;
		}
		setRole(guildId, userId, newRole);
	}

	/**
	 * Evaluates which {@link Role} the user should have in the given guild based on the given member roles.
	 */
	private Role evaluateRole(@NonNull Guild guild, Set<Long> memberRoles) {
		if (guild.getAdminRole() != null && memberRoles.contains(guild.getAdminRole())) {
			return Role.ADMINISTRATOR;
		} else if (guild.getEventManageRole() != null && memberRoles.contains(guild.getEventManageRole())) {
			return Role.EVENT_MANAGE;
		}
		return null;
	}

	@EventListener
	@Async
	public void onGuildUserCreatedEvent(@NonNull GuildUserCreatedEvent event) {
		invalidateSession(event.userId());
	}

	@EventListener
	@Async
	public void onGuildUserRoleUpdateEvent(@NonNull GuildUserRoleUpdateEvent event) {
		invalidateSession(event.userId());
	}

	@EventListener
	@Async
	public void onGuildUserDeleteEvent(@NonNull GuildUserDeleteEvent event) {
		invalidateSession(event.userId());
	}

	private void invalidateSession(long userId) {
		log.trace("Invalidating session for user {}", userId);
		sessionRegistry.getAllPrincipals().stream()
				.flatMap(principal -> sessionRegistry.getAllSessions(principal, false).stream())
				.filter(sessionInformation -> sessionInformation.getPrincipal() instanceof final DefaultOAuth2User user
						&& user.getAttributes().get("id").equals(Long.toString(userId)))
				.forEach(SessionInformation::expireNow);
	}

	public Set<String> getApplicationRoles(long userId) {
		Set<String> roles = new HashSet<>();

		findByUserId(userId)
				.forEach(guildUser -> {
					final String role = guildUser.getApplicationRole();
					if (role == null) {
						return;
					}
					roles.add(buildAuthenticationWithPrefix(role)); //Add role for potential checks
					roles.add(buildGuildAuthenticationWithPrefix(role, guildUser.getId().getGuildId()));
				});

		globalRoleRepository.findAllByUserId(userId).stream()
				.map(globalRole -> buildAuthenticationWithPrefix(globalRole.getRole().getApplicationRole()))
				.forEach(roles::add);

		return roles;
	}
}
