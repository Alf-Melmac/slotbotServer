package de.webalf.slotbot.service;

import de.webalf.slotbot.controller.website.StartWebController;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 23.09.2021
 */
@Service
@ControllerAdvice(annotations = Controller.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ControllerAdviser {
	private final GuildService guildService;

	@ModelAttribute
	public void handleRequest(ModelAndView mav) {
		mav.addObject("startUrl", linkTo(methodOn(StartWebController.class).getStart()).toUri().toString());
		mav.addObject("group", guildService.getCurrentGroupIdentifier());
	}
}
