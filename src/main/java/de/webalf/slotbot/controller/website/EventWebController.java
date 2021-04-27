package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.assembler.website.EventDetailsAssembler;
import de.webalf.slotbot.controller.EventController;
import de.webalf.slotbot.model.dtos.website.EventDetailsDto;
import de.webalf.slotbot.service.EventService;
import de.webalf.slotbot.util.DiscordMarkdown;
import de.webalf.slotbot.util.LongUtils;
import de.webalf.slotbot.util.permissions.BotPermissionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_ROLE_CREATOR;
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
	private final EventService eventService;
	private final EventDetailsAssembler eventDetailsAssembler;

	private static final String START_URL_STRING = "startUrl";
	private static final String START_URL = linkTo(methodOn(StartWebController.class).getStart()).toUri().toString();
	private static final String EVENTS_URL_STRING = "eventsUrl";
	private static final String EVENTS_URL = linkTo(methodOn(EventWebController.class).getEventHtml()).toUri().toString();
	private static final String LOGIN_URL_STRING = "loginUrl";
	private static final String LOGIN_URL = linkTo(methodOn(LoginWebController.class).getLogin()).toUri().toString();

	@GetMapping
	public ModelAndView getEventHtml() {
		ModelAndView mav = new ModelAndView("events");

		mav.addObject("getEventsUrl", linkTo(methodOn(EventController.class).getBetween(null, null))
				.toUri().toString()
				//Remove parameters, because the calendar adds them by itself
				.split("\\?")[0]);
		mav.addObject("createEventUrl", linkTo(methodOn(EventWebController.class).getWizardHtml()).toUri().toString());
		mav.addObject(START_URL_STRING, START_URL);
		mav.addObject("eventManageRoles", BotPermissionHelper.getEventManageApplicationRoles());

		return mav;
	}

	@GetMapping("/new")
	@PreAuthorize(HAS_ROLE_CREATOR)
	public ModelAndView getWizardHtml() {
		ModelAndView mav = new ModelAndView("eventWizard");

		mav.addObject(START_URL_STRING, START_URL);
		mav.addObject(EVENTS_URL_STRING, EVENTS_URL);
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
		mav.addObject(LOGIN_URL_STRING, LOGIN_URL);
		mav.addObject(EVENTS_URL_STRING, EVENTS_URL);
		final EventDetailsDto detailsDto = eventDetailsAssembler.toDto(eventService.findById(eventId));
		mav.addObject("event", detailsDto);
		mav.addObject("eventDescriptionHtml", DiscordMarkdown.toHtml(detailsDto.getDescription()));
		mav.addObject("eventEditUrl", linkTo(methodOn(EventWebController.class).getEventEditHtml(eventId)).toUri().toString());
		mav.addObject("hasEventManageRole", BotPermissionHelper.hasEventManageRole());
		return mav;
	}

	@GetMapping("/{id}/edit")
	@PreAuthorize(HAS_ROLE_CREATOR)
	public ModelAndView getEventEditHtml(@PathVariable(value = "id") long eventId) {
		ModelAndView mav = new ModelAndView("eventEdit");

		mav.addObject(START_URL_STRING, START_URL);
		mav.addObject(EVENTS_URL_STRING, EVENTS_URL);
		mav.addObject("putEventUrl", linkTo(methodOn(EventController.class).updateEvent(eventId, null)).toUri().toString());
		mav.addObject("putEventEditableUrl", linkTo(methodOn(EventController.class).updateEventEditable(eventId, null, null)).toUri().toString());
		mav.addObject("eventDetailsUrl", linkTo(methodOn(EventWebController.class).getEventDetailsHtml(eventId)).toUri().toString());
		mav.addObject("event", eventDetailsAssembler.toDto(eventService.findById(eventId)));
		return mav;
	}
}
