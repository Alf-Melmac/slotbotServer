package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.assembler.CalendarEventAssembler;
import de.webalf.slotbot.controller.WebsiteController;
import de.webalf.slotbot.model.dtos.website.CalendarEventDto;
import de.webalf.slotbot.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 24.10.2020
 */
@Controller
@RequestMapping("/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventsController {
	private final EventRepository eventRepository;

	@GetMapping
	public ModelAndView getEventHtml() {
		ModelAndView mav = new ModelAndView("events");

		mav.addObject("getEventsUrl", linkTo(methodOn(EventsController.class).getBetween(null, null))
				.toUri().toString()
				.split("\\?")[0]);
		mav.addObject("startUrl", linkTo(methodOn(WebsiteController.class).getStart()).toUri().toString());

		return mav;
	}

	@GetMapping(value = "/list")
	@ResponseBody //Needed to prevent Thymeleaf trying to interpret this endpoint. May consider using @RestController
	public List<CalendarEventDto> getBetween(@RequestParam LocalDateTime start,
	                                         @RequestParam LocalDateTime end) {
		return CalendarEventAssembler.toDtoList(eventRepository.findAllByDateTimeBetween(start, end));
	}
}
