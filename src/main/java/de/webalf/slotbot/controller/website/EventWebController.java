package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.assembler.EventAssembler;
import de.webalf.slotbot.controller.EventController;
import de.webalf.slotbot.controller.Urls;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.repository.EventRepository;
import de.webalf.slotbot.util.LongUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	private final EventRepository eventRepository;

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
	public ModelAndView getWizardHtml() {
		ModelAndView mav = new ModelAndView("eventWizard");

		mav.addObject("startUrl", Urls.START_URL);
		mav.addObject("eventsUrl", linkTo(methodOn(EventWebController.class).getEventHtml()).toUri().toString());
		mav.addObject("postEventUrl", linkTo(methodOn(EventController.class).postEvent(null)).toUri().toString());
		mav.addObject("eventDetailsUrl", linkTo(methodOn(EventWebController.class)
				.getEventDetailsHtml(Long.MIN_VALUE))
				.toUri().toString()
				.replace(LongUtils.toString(Long.MIN_VALUE), "{eventId}"));
		return mav;
	}

	@GetMapping("{id}")
	public ModelAndView getEventDetailsHtml(@PathVariable(value = "id") long eventId) {
		ModelAndView mav = new ModelAndView("eventDetails");

		mav.addObject("startUrl", Urls.START_URL);
		Event event = eventRepository.findById(eventId).orElseThrow(ResourceNotFoundException::new);
		mav.addObject("event", EventAssembler.toDto(event));
		mav.addObject("defaultUserId", LongUtils.toString(User.DEFAULT_USER_ID));
		return mav;
	}
}
