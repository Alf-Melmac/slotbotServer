package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.GuildUser;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.repository.GlobalRoleRepository;
import de.webalf.slotbot.repository.GuildUsersRepository;
import de.webalf.slotbot.util.permissions.Role;
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

		return guildUsersRepository.findByGuildAndUser(guild, user)
				.orElseGet(() -> guildUsersRepository.save(GuildUser.builder().user(user).guild(guild).build()));
	}

	public void setRole(long guildId, long userId, Role role) {
		sessionRegistry.getAllPrincipals().stream()
				.flatMap(principal -> sessionRegistry.getAllSessions(principal, false).stream())
				.filter(sessionInformation -> sessionInformation.getPrincipal() instanceof final DefaultOAuth2User user
						&& user.getAttributes().get("id").equals(Long.toString(userId)))
				.forEach(SessionInformation::expireNow);
		guildUsersRepository.updateRoleByGuildAndUser(role, guildId, userId);
	}

	public void remove(long guildId, long userId) {
		final User user = userService.findExisting(userId);
		final Guild guild = guildService.findExisting(guildId);

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
}
