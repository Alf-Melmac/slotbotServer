package de.webalf.slotbot.controller.website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Alf
 * @since 09.11.2020
 */
@Controller
public class RedirectController {
	@GetMapping("/events")
	public RedirectView redirectToEvents() {
		return new RedirectView(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString() + "/events");
	}
}
