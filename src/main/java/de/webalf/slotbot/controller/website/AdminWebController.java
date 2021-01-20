package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.service.external.BattlemetricsApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

import static de.webalf.slotbot.controller.Urls.ADMIN;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 30.12.2020
 */
@Controller
@RequestMapping(ADMIN)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AdminWebController {
	private final BattlemetricsApiService battlemetricsApiService;

	@GetMapping
	public ModelAndView getAdminHtml() {
		ModelAndView mav = new ModelAndView("admin");

		mav.addObject("startUrl", linkTo(methodOn(StartWebController.class).getStart()).toUri().toString());
		mav.addObject("logsUrl", linkTo(methodOn(LogWebController.class).getLogsHtml()).toUri().toString());

		mav.addObject("servers", battlemetricsApiService.getServers());
		mav.addObject("pingTime", LocalDateTime.now());

		return mav;
	}
}
