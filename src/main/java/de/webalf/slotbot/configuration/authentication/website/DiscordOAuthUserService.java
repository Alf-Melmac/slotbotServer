package de.webalf.slotbot.configuration.authentication.website;

import de.webalf.slotbot.model.event.BanEvent;
import de.webalf.slotbot.service.GuildUsersService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Alf
 * @since 11.10.2026
 */
@Service
@RequiredArgsConstructor
public class DiscordOAuthUserService {
	private final SessionRegistry sessionRegistry;
	private final GuildUsersService guildUsersService;

	/**
	 * Returns all non-expired sessions of the given user
	 *
	 * @param userId id of the user
	 * @return sessions of the user
	 */
	public Stream<SessionInformation> findSessions(long userId) {
		return sessionRegistry.getAllPrincipals().stream()
				.filter(principal -> principal instanceof final DiscordOAuthUser user && user.getId() == userId)
				.flatMap(principal -> sessionRegistry.getAllSessions(principal, false).stream());
	}

	/**
	 * Returns the {@link GuildUsersService#getApplicationRoles(long) current application roles} of the given user as
	 * authorities
	 * <p>
	 * Note: Does not include Spring provided authorities (OAUTH2_USER, SCOPE_*)
	 *
	 * @param userId id of the user
	 * @return application authorities
	 */
	Set<GrantedAuthority> getApplicationAuthorities(long userId) {
		return guildUsersService.getApplicationRoles(userId).stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toUnmodifiableSet());
	}

	@EventListener
	@Async
	public void onBanEvent(@NonNull BanEvent event) {
		if (event.guildId() != null) return; // Only process global bans

		findSessions(event.userId()).forEach(SessionInformation::expireNow);
	}
}
