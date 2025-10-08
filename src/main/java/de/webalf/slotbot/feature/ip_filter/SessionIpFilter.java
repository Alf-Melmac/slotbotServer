package de.webalf.slotbot.feature.ip_filter;

import de.webalf.slotbot.service.web.FeatureFlagService;
import de.webalf.slotbot.util.permissions.PermissionHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Ensures that one session is only used from one ip
 *
 * @author Alf
 * @since 02.08.2024
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SessionIpFilter extends OncePerRequestFilter {
	/**
	 * Session attribute to store an additional IP address to the authentication details remote address.
	 * This is to allow switching between IPv4 and IPv6 within the same session.
	 * <p>
	 * Contains either an IPv4 or an IPv6 address.
	 */
	private static final String SESSION_ATTRIBUTE_OTHER_IP = "OTHER_IP";
	/**
	 * Session attribute to store if the current session is using Apple iCloud Private Relay.
	 * <ul>
	 *  <li>{@code null} Not yet determined</li>
	 *  <li>{@code true} Is Private Relay</li>
	 *  <li>{@code false} Is not Private Relay</li>
	 * </ul>
	 */
	private static final String SESSION_ATTRIBUTE_PRIVATE_RELAY = "PRIVATE_RELAY";

	private final FeatureFlagService featureFlagService;
	private final PrivateRelayRangeService privateRelayRangeService;

	@Override
	@SuppressWarnings("java:S3776") // Filters are more useful if everything is in one method
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			filterChain.doFilter(request, response);
			return;
		}
		final HttpSession session = request.getSession(false);
		if (session == null || !(authentication.getDetails() instanceof final WebAuthenticationDetails details)) {
			filterChain.doFilter(request, response);
			return;
		}

		final String loginIp = details.getRemoteAddress();
		final String currentIp = request.getRemoteAddr();

		// If the authentication request ip and the current request ip are the same, allow
		if (loginIp.equals(currentIp)) {
			filterChain.doFilter(request, response);
			return;
		}
		// -> Ips don't match

		// Private Relay handling
		if (!Boolean.FALSE.equals(session.getAttribute(SESSION_ATTRIBUTE_PRIVATE_RELAY))) {
			if (privateRelayRangeService.isFromPrivateRelay(currentIp)) {
				log.debug("Session of {} detected as Private Relay: {}", PermissionHelper.getLoggedInUserId(), currentIp);
				session.setAttribute(SESSION_ATTRIBUTE_PRIVATE_RELAY, true);
				if (featureFlagService.getGlobal("sessionIpFilterPrivateRelay")) {
					filterChain.doFilter(request, response);
					return;
				}
			} else {
				log.trace("Session of {} not detected as Private Relay: {}", PermissionHelper.getLoggedInUserId(), currentIp);
				session.setAttribute(SESSION_ATTRIBUTE_PRIVATE_RELAY, false);
			}
		}

		// If both ips are of the same version, invalidate session
		if (loginIp.contains(":") == currentIp.contains(":")) {
			invalidateSession(session, loginIp, loginIp, currentIp);
			return;
		}
		// -> Ip versions are different

		final String otherIp = (String) session.getAttribute(SESSION_ATTRIBUTE_OTHER_IP);
		// First time seeing a different ip version for this session, store it and allow
		if (otherIp == null) {
			session.setAttribute(SESSION_ATTRIBUTE_OTHER_IP, currentIp);
			log.info("Session of {} used a different ip version: {} -> {}", PermissionHelper.getLoggedInUserId(), loginIp, currentIp);
			filterChain.doFilter(request, response);
			return;
		}
		// -> Different ip version already stored in session

		// Validate the other ip
		if (otherIp.equals(currentIp)) {
			filterChain.doFilter(request, response);
		} else {
			invalidateSession(session, otherIp, loginIp, currentIp);
		}
	}

	private static void invalidateSession(HttpSession session, String storedIp, String loginIp, String currentIp) {
		try {
			session.invalidate();
			log.warn("Session of {} invalidated due to ip change: {} ({}) -> {}", PermissionHelper.getLoggedInUserId(), storedIp, loginIp, currentIp);
		} catch (IllegalStateException ignored) {
			// Session already invalidated
		}
	}
}
