package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.util.StringUtils;
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
		String baseRedirectUrl = guildService.findCurrentNonNullGuild().getBaseRedirectUrl();
		return new ModelAndView("redirect:" + (StringUtils.isNotEmpty(baseRedirectUrl) ? baseRedirectUrl : "http://localhost:3000"));
	}

	@GetMapping("/error/403")
	public ModelAndView getUnauthorized() {
		return new ModelAndView("error/403");
	}
}
