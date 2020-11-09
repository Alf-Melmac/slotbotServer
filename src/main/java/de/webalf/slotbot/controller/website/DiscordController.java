package de.webalf.slotbot.controller.website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Alf
 * @since 09.11.2020
 */
@Controller
@RequestMapping("/discord")
public class DiscordController {

	@GetMapping
	public RedirectView redirectToDiscord() {
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl("https://discord.gg/fZCTze5VD9");
		return redirectView;
	}
}
