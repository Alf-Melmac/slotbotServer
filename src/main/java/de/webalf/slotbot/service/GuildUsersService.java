package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.GuildUser;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.repository.GlobalRoleRepository;
import de.webalf.slotbot.repository.GuildUsersRepository;
import de.webalf.slotbot.util.permissions.Role;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
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
public class GuildUsersService {
	private final GuildUsersRepository guildUsersRepository;
	private final UserService userService;
	private final GuildService guildService;
	private final GlobalRoleRepository globalRoleRepository;
	private final SessionRegistry sessionRegistry;

	public Page<GuildUser> findGuildUsers(Guild guild, Pageable pageable) {
		return guildUsersRepository.findByGuild(guild, pageable);
	}

	public Set<GuildUser> findByUserId(long userId) {
		return guildUsersRepository.findByIdUserId(userId);
	}

	public GuildUser add(long guildId, long userId) {
		final Guild guild = guildService.find(guildId);
		final User user = userService.find(userId);

		invalidateSession(userId);

		return guildUsersRepository.findByGuildAndUser(guild, user)
				.orElseGet(() -> guildUsersRepository.save(GuildUser.builder().user(user).guild(guild).build()));
	}

	public void remove(long guildId, long userId) {
		final User user = userService.findExisting(userId);
		final Guild guild = guildService.findExisting(guildId);

		invalidateSession(userId);

		guildUsersRepository.deleteByGuildAndUser(guild, user);
	}

	@Async
	public void removeOptional(long guildId, long userId) {
		final Optional<User> user = userService.findExistingOptional(userId);
		if (user.isEmpty()) {
			return;
		}
		final Optional<Guild> guild = guildService.findExistingOptional(guildId);
		if (guild.isEmpty()) {
			return;
		}

		guildUsersRepository.deleteByGuildAndUser(guild.get(), user.get());
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

		invalidateSession(userId);

		guildUsersRepository.findByGuildAndUser(guild, user)
				.ifPresentOrElse(guildUser -> guildUser.setRole(role),
						() -> guildUsersRepository.save(GuildUser.builder().guild(guild).user(user).role(role).build()));
	}

	private void invalidateSession(long userId) {
		sessionRegistry.getAllPrincipals().stream()
				.flatMap(principal -> sessionRegistry.getAllSessions(principal, false).stream())
				.filter(sessionInformation -> sessionInformation.getPrincipal() instanceof final DefaultOAuth2User user
						&& user.getAttributes().get("id").equals(Long.toString(userId)))
				.forEach(SessionInformation::expireNow);
	}

	@Async
	public void onRolesAdded(long guildId, long userId, Set<Long> addedRoles, Set<Long> memberRoles) {
		guildService.findByIdAndAnyRoleIn(guildId, addedRoles).ifPresent(guild ->
				setRole(guildId, userId, evaluateRole(guild, memberRoles)));
	}

	@Async
	public void onRolesRemoved(long guildId, long userId, Set<Long> removedRoles, Set<Long> memberRoles) {
		guildService.findByIdAndAnyRoleIn(guildId, removedRoles).ifPresent(guild -> {
			final Role newRole = evaluateRole(guild, memberRoles);
			//If a member role is configured and no role is left, remove user from guild
			if (guild.getMemberRole() != null && !memberRoles.contains(guild.getMemberRole()) && newRole == null) {
				remove(guildId, userId);
				return;
			}
			setRole(guildId, userId, newRole);
		});
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
}
