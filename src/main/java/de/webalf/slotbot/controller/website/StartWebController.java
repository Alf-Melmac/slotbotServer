package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.service.GuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Alf
 * @since 27.09.2020
 */
@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StartWebController {
	private final GuildService guildService;

	@GetMapping
	public ModelAndView getStart() {
		if (!guildService.isAMB()) {
			return new ModelAndView("redirect:" + guildService.findCurrentNonNullGuild().getBaseRedirectUrl());
		}

		return new ModelAndView("start");
	}

	@GetMapping("/error/403")
	public ModelAndView getUnauthorized() {
		return new ModelAndView("error/403");
	}
}
