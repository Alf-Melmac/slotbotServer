package de.webalf.slotbot.service;

import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.GuildUser;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.event.BanEvent;
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
import java.util.List;
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
	private final BanService banService;

	public Page<GuildUser> findGuildUsers(Guild guild, Pageable pageable) {
		return guildUsersRepository.findByGuild(guild, pageable);
	}

	public Set<GuildUser> findByUserId(long userId) {
		return guildUsersRepository.findByIdUserId(userId);
	}

	/**
	 * Finds all user IDs with admin role in the given guild
	 *
	 * @param guild to find admins in
	 * @return user ids with admin role
	 */
	public Set<Long> findByGuildAndAdmin(@NonNull Guild guild) {
		return guildUsersRepository.findByGuildAndRole(guild, Role.ADMINISTRATOR);
	}

	public GuildUser add(long guildId, long userId) {
		return add(guildId, userId, null);
	}

	public GuildUser add(long guildId, long userId, Role role) {
		final Guild guild = guildService.find(guildId);
		final User user = userService.find(userId);
		if (banService.isBanned(user, guild)) {
			throw BusinessRuntimeException.builder()
					.title("Um dieses Mitglied der Community hinzufügen zu können, muss erst der Ban aufgehoben werden.")
					.build();
		}

		log.trace("Adding user {} to guild {} with role {}", userId, guildId, role);
		return guildUsersRepository.findByGuildAndUser(guild, user)
				.orElseGet(() -> create(guild, user, role));
	}

	private GuildUser create(@NonNull Guild guild, @NonNull User user, Role role) {
		final GuildUser guildUser = guildUsersRepository.save(GuildUser.builder().user(user).guild(guild).role(role).build());
		eventPublisher.publishEvent(new GuildUserCreatedEvent(guildUser));
		return guildUser;
	}

	/**
	 * @see #remove(Guild, User)
	 */
	public void remove(long guildId, long userId) {
		final User user = userService.findExisting(userId);
		final Guild guild = guildService.findExisting(guildId);

		remove(guild, user);
	}

	private void remove(@NonNull Guild guild, @NonNull User user) {
		log.trace("Removing user {} from guild {}", user.getId(), guild.getId());
		guildUsersRepository.findByGuildAndUser(guild, user)
				.ifPresent(guildUser -> {
					eventPublisher.publishEvent(new GuildUserDeleteEvent(guildUser));
					guildUsersRepository.delete(guildUser);
				});
	}

	@Async
	public void removeOptional(long guildId, long userId) {
		guildUsersRepository.deleteById_GuildIdAndId_UserId(guildId, userId);
		invalidateSession(userId);
	}

	/**
	 * Removes all users except the given ones from the given guild
	 *
	 * @param guild      to remove users from
	 * @param userIds    to keep
	 * @param keepAdmins if true, users with admin role will not be removed
	 */
	public void removeExcept(@NonNull Guild guild, Set<Long> userIds, boolean keepAdmins) {
		final List<GuildUser> removed = keepAdmins
				? guildUsersRepository.deleteByGuildAndId_UserIdNotInAndRoleNot(guild, userIds, Role.ADMINISTRATOR)
				: guildUsersRepository.deleteByGuildAndId_UserIdNotIn(guild, userIds);
		removed.forEach(guildUser -> eventPublisher.publishEvent(new GuildUserDeleteEvent(guildUser)));
	}

	public void ban(long guildId, long userId, String reason) {
		banService.ban(userId, guildId, reason);
		remove(guildId, userId);
	}

	/**
	 * @see #setRole(Guild, User, Role)
	 */
	public void setRole(long guildId, long userId, Role role) {
		final Guild guild = guildService.find(guildId);
		final User user = userService.find(userId);
		setRole(guild, user, role);
	}

	/**
	 * Sets the given role for the given user in the given guild
	 *
	 * @param guild guild to set role for
	 * @param user  user with role
	 * @param role  to set
	 */
	private void setRole(@NonNull Guild guild, @NonNull User user, Role role) {
		log.trace("Setting role {} for user {} in guild {}", role, user.getId(), guild.getId());
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
		final User user = userService.find(userId);

		if (banService.isBanned(user, guild)) {
			log.trace("Skipping role change because user {} is banned in guild {}", userId, guildId);
			return;
		}

		final Role newRole = evaluateRole(guild, memberRoles);
		//If a member role is configured and no role is left, remove user from guild
		if (guild.getMemberRole() != null && !memberRoles.contains(guild.getMemberRole()) && newRole == null) {
			remove(guild, user);
			return;
		}
		setRole(guild, userService.find(userId), newRole);
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

	@EventListener
	@Async
	public void onBanEvent(@NonNull BanEvent event) {
		if (event.guildId() != null) return; // This has been triggered by #ban(long, long, String)

		final long userId = event.userId();
		guildUsersRepository.deleteById_UserId(userId);
		invalidateSession(userId);
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
