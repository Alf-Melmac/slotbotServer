package de.webalf.slotbot.controller.website;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
	@GetMapping
	public ModelAndView getAdminHtml() {
		ModelAndView mav = new ModelAndView("admin");

		mav.addObject("startUrl", linkTo(methodOn(StartWebController.class).getStart()).toUri().toString());
		mav.addObject("logsUrl", linkTo(methodOn(LogWebController.class).getLogsHtml()).toUri().toString());

		//TODO Admin start page rework
		final HttpStatus ping = HttpStatus.OK;
		mav.addObject("status", ping != null ? ping.value() + " " + ping.getReasonPhrase() : "No status received. Rejected?");
		mav.addObject("ping", ping != null && ping.is2xxSuccessful());
		mav.addObject("pingTime", LocalDateTime.now());

		return mav;
	}
}
