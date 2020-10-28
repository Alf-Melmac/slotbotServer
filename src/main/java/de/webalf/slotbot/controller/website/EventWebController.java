package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.configuration.authentication.api.TokenProvider;
import de.webalf.slotbot.controller.EventController;
import de.webalf.slotbot.controller.Urls;
import de.webalf.slotbot.controller.api.EventApiController;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 24.10.2020
 */
@Controller
@RequestMapping("/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventWebController {
	private final TokenProvider tokenProvider;

	@GetMapping
	public ModelAndView getEventHtml() {
		ModelAndView mav = new ModelAndView("events");

		mav.addObject("getEventsUrl", linkTo(methodOn(EventController.class).getBetween(null, null))
				.toUri().toString()
				//Remove parameters, because the calendar adds them by itself
				.split("\\?")[0]);
		mav.addObject("startUrl", Urls.START_URL);

		return mav;
	}

	@GetMapping("/new")
	public ModelAndView getWizard() {
		ModelAndView mav = new ModelAndView("eventWizard");

		mav.addObject("startUrl", Urls.START_URL);
		mav.addObject("eventsUrl", linkTo(methodOn(EventWebController.class).getEventHtml()).toUri().toString());
		mav.addObject("postEventUrl", linkTo(methodOn(EventApiController.class).postEvent(null)).toUri().toString());
		mav.addObject("slotbotAuthTokenName", tokenProvider.getTokenName());
		mav.addObject("slotbotAuthToken", tokenProvider.getSlotbotKey());
		return mav;
	}
}
