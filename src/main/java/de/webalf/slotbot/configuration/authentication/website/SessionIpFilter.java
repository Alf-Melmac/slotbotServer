package de.webalf.slotbot.configuration.authentication.website;

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
	private final FeatureFlagService featureFlagService;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			final HttpSession session = request.getSession(false);
			if (session != null && authentication.getDetails() instanceof final WebAuthenticationDetails details) {
				final String remoteAddress = details.getRemoteAddress();
				if (!remoteAddress.equals(request.getRemoteAddr())) {
					log.warn("Session of {} invalidated due to ip change: {} -> {}", PermissionHelper.getLoggedInUserId(), remoteAddress, request.getRemoteAddr());
					if (featureFlagService.getGlobal("sessionIpFilter")) {
						session.invalidate();
						return;
					}
				}
			}
		}
		filterChain.doFilter(request, response);
	}
}
