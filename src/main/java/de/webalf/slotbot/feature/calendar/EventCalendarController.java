package de.webalf.slotbot.feature.calendar;

import de.webalf.slotbot.feature.calendar.dto.CalendarEventDto;
import de.webalf.slotbot.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Alf
 * @since 21.10.25
 */
@RestController
@RequestMapping("/events/calendar")
@RequiredArgsConstructor
@Slf4j
public class EventCalendarController {
	private final EventService eventService;

	@GetMapping({"", "/{guild}"})
	public List<CalendarEventDto> getBetween(@PathVariable(required = false) Optional<String> guild,
	                                         @RequestParam LocalDateTime start,
	                                         @RequestParam LocalDateTime end) {
		return CalendarEventAssembler.toDtoList(eventService.findAllBetween(start, end, guild));
	}

	@GetMapping("/{guild}/around-today")
	@PreAuthorize("@permissionChecker.isAdvancedGuild(#guild)")
	public List<CalendarEventDto> getAroundToday(@PathVariable String guild) {
		return CalendarEventAssembler.toDtoList(eventService.findAllAroundToday(guild));
	}
}
