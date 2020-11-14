package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.assembler.website.EventDetailsAssembler;
import de.webalf.slotbot.controller.EventController;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.repository.EventRepository;
import de.webalf.slotbot.service.PermissionService;
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
	private final EventDetailsAssembler eventDetailsAssembler;

	private static final String START_URL_STRING = "startUrl";
	private static final String START_URL = linkTo(methodOn(StartWebController.class).getStart()).toUri().toString();

	@GetMapping
	public ModelAndView getEventHtml() {
		ModelAndView mav = new ModelAndView("events");

		mav.addObject("getEventsUrl", linkTo(methodOn(EventController.class).getBetween(null, null))
				.toUri().toString()
				//Remove parameters, because the calendar adds them by itself
				.split("\\?")[0]);
		mav.addObject("createEventUrl", linkTo(methodOn(EventWebController.class).getWizardHtml()).toUri().toString());
		mav.addObject(START_URL_STRING, START_URL);
		mav.addObject("eventManageRoles", PermissionService.getEventManageRoles());

		return mav;
	}

	@GetMapping("/new")
	public ModelAndView getWizardHtml() {
		ModelAndView mav = new ModelAndView("eventWizard");

		mav.addObject(START_URL_STRING, START_URL);
		mav.addObject("eventsUrl", linkTo(methodOn(EventWebController.class).getEventHtml()).toUri().toString());
		mav.addObject("postEventUrl", linkTo(methodOn(EventController.class).postEvent(null)).toUri().toString());
		mav.addObject("eventDetailsUrl", linkTo(methodOn(EventWebController.class)
				.getEventDetailsHtml(Long.MIN_VALUE))
				.toUri().toString()
				.replace(LongUtils.toString(Long.MIN_VALUE), "{eventId}"));
		return mav;
	}

	@GetMapping("/{id}")
	public ModelAndView getEventDetailsHtml(@PathVariable(value = "id") long eventId) {
		ModelAndView mav = new ModelAndView("eventDetails");

		mav.addObject(START_URL_STRING, START_URL);
		mav.addObject("eventsUrl", linkTo(methodOn(EventWebController.class).getEventHtml()).toUri().toString());
		Event event = eventRepository.findById(eventId).orElseThrow(ResourceNotFoundException::new);
		mav.addObject("event", eventDetailsAssembler.toDto(event));
		mav.addObject("eventEditUrl", linkTo(methodOn(EventWebController.class).getEventEditHtml(eventId)).toUri().toString());
		mav.addObject("hasEventManageRole", PermissionService.hasEventManageRole());
		return mav;
	}

	@GetMapping("/edit/{id}")
	public ModelAndView getEventEditHtml(@PathVariable(value = "id") long eventId) {
		ModelAndView mav = new ModelAndView("eventEdit");

		mav.addObject(START_URL_STRING, START_URL);
		mav.addObject("eventsUrl", linkTo(methodOn(EventWebController.class).getEventHtml()).toUri().toString());
		mav.addObject("putEventUrl", linkTo(methodOn(EventController.class).updateEvent(eventId, null)).toUri().toString());
		mav.addObject("eventDetailsUrl", linkTo(methodOn(EventWebController.class).getEventDetailsHtml(eventId)).toUri().toString());
		Event event = eventRepository.findById(eventId).orElseThrow(ResourceNotFoundException::new);
		mav.addObject("event", eventDetailsAssembler.toDto(event));
		return mav;
	}
}
