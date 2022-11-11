package de.webalf.slotbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Alf
 * @since 23.09.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Deprecated
public class ControllerAdviser implements HandlerInterceptor {
	private final GuildService guildService;

	@Override
	@Deprecated
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
		if (modelAndView != null && modelAndView.hasView() && !(modelAndView.getView() instanceof RedirectView)) {
			modelAndView.addObject("startUrl", "http://localhost:3000");
			modelAndView.addObject("group", guildService.getCurrentGroupIdentifier());
		}
	}
}
