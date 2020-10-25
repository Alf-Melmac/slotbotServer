package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.controller.EventController;
import de.webalf.slotbot.controller.Urls;
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
public class EventWebController {
	@GetMapping
	public ModelAndView getEventHtml() {
		ModelAndView mav = new ModelAndView("events");

		mav.addObject("getEventsUrl", linkTo(methodOn(EventController.class).getBetween(null, null))
				.toUri().toString()
				.split("\\?")[0]);
		mav.addObject("startUrl", Urls.START_URL);

		return mav;
	}
}
