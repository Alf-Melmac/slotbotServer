package de.webalf.slotbot.controller.website;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Alf
 * @since 09.11.2020
 */
@Controller
@Profile("!dev")
public class RedirectController {
	@Value("#{servletContext.contextPath}")
	private String servletContextPath;

	@GetMapping("/events") //OAuth2EndpointConfig logoutSuccessUrl
	public RedirectView redirectToEvents() {
		final String url = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString() + "/events";
		return new RedirectView(url.replace(servletContextPath, ""));
	}
}
