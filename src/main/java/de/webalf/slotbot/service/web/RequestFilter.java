package de.webalf.slotbot.service.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author Alf
 * @since 25.09.2024
 */
@Component
@Slf4j
public class RequestFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
		final String uri = request.getRequestURI();
		log.info("Request intercepted: {}", uri);
		if (uri.contains("/yoot")) {
			request.setAttribute("tenant", "yoot");
			final String newUri = uri.replace("/yoot", "");
			TenantRequestWrapper wrappedRequest = new TenantRequestWrapper(request, newUri);
			request.getRequestDispatcher(newUri).forward(wrappedRequest, response);
			return;
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * Wrapper of the request to change the uri
	 */
	private static class TenantRequestWrapper extends HttpServletRequestWrapper {
		private final String newUri;

		public TenantRequestWrapper(HttpServletRequest request, String newUri) {
			super(request);
			this.newUri = newUri;
		}

		@Override
		public String getRequestURI() {
			return newUri;
		}
	}
}
