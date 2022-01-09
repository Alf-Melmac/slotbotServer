package de.webalf.slotbot.service;

import de.webalf.slotbot.controller.website.StartWebController;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 23.09.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ControllerAdviser implements HandlerInterceptor {
	private final GuildService guildService;

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
		if (modelAndView != null && modelAndView.hasView() && !(modelAndView.getView() instanceof RedirectView)) {
			modelAndView.addObject("startUrl", linkTo(methodOn(StartWebController.class).getStart()).toUri().toString());
			modelAndView.addObject("group", guildService.getCurrentGroupIdentifier());
		}
	}
}
