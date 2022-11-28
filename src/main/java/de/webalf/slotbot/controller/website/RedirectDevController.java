package de.webalf.slotbot.controller.website;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Alf
 * @since 27.11.2022
 */
@Controller
@Profile("dev")
public class RedirectDevController {
	@GetMapping("/events")
	public RedirectView redirectToEventsInDev() {
		return new RedirectView(ServletUriComponentsBuilder
				.fromCurrentContextPath()
				.port(3000)
				.toUriString() +
				"/events"
		);
	}
}
