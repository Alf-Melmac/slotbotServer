package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.service.web.RedirectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Alf
 * @since 09.11.2020
 */
@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RedirectController {
	private final RedirectService redirectService;

	@GetMapping("/events") //OAuth2EndpointConfig logoutSuccessUrl
	public RedirectView redirectToEvents() {
		return new RedirectView(redirectService.redirectTo("/events"));
	}
}
