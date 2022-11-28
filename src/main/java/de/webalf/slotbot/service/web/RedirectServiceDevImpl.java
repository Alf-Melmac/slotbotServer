package de.webalf.slotbot.service.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * @author Alf
 * @since 28.11.2022
 */
@Service
@Profile("dev")
public class RedirectServiceDevImpl implements RedirectService {
	@Value("#{servletContext.contextPath}")
	private String servletContextPath;

	@Override
	public String redirectTo(String redirectPath) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().port(3000).toUriString()
				.replace(servletContextPath, "")
				+ redirectPath;
	}
}
