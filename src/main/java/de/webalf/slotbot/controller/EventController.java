package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.EventAssembler;
import de.webalf.slotbot.assembler.website.CalendarEventAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.model.dtos.EventFieldDefaultDto;
import de.webalf.slotbot.model.dtos.referenceless.EventReferencelessDto;
import de.webalf.slotbot.model.dtos.website.CalendarEventDto;
import de.webalf.slotbot.service.EventService;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.util.permissions.PermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static de.webalf.slotbot.util.eventfield.EventFieldUtils.getDefault;
import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_POTENTIALLY_ROLE_EVENT_MANAGE;

/**
 * @author Alf
 * @since 25.10.2020
 */
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventController {
	private final PermissionChecker permissionChecker;
	private final GuildService guildService;
	private final EventService eventService;

	@GetMapping(value = "/list")
	public List<CalendarEventDto> getBetween(@RequestParam LocalDateTime start,
											 @RequestParam LocalDateTime end) {
		return CalendarEventAssembler.toDtoList(eventService.findAllBetweenOfGuild(start, end, guildService.findCurrentNonNullGuild()));
	}

	@PostMapping
	@PreAuthorize("@permissionChecker.assertEventManagePermission(#event.getOwnerGuild())")
	public EventReferencelessDto postEvent(@Valid @RequestBody EventDto event) {
		return EventAssembler.toReferencelessDto(eventService.createEvent(event));
	}

	@PutMapping("/{id}")
	@PreAuthorize(HAS_POTENTIALLY_ROLE_EVENT_MANAGE)
	public EventReferencelessDto updateEvent(@PathVariable(value = "id") long eventId, @RequestBody EventDto event) {
		permissionChecker.assertEventManagePermission(eventService.getGuildByEventId(eventId));

		event.setId(eventId);
		return EventAssembler.toReferencelessDto(eventService.updateEvent(event));
	}

	@PostMapping("/editable")
	@PreAuthorize(HAS_POTENTIALLY_ROLE_EVENT_MANAGE)
	public EventReferencelessDto updateEventEditable(long pk, String name, String value) {
		permissionChecker.assertEventManagePermission(eventService.getGuildByEventId(pk));

		EventDto dto = EventDto.builder().id(pk).build();
		try {
			ReflectionUtils.setField(dto.getClass().getSuperclass().getDeclaredField(name), dto, value.trim());
		} catch (NoSuchFieldException e) {
			log.error("Can't find field " + name + " while trying to edit it.", e);
			throw BusinessRuntimeException.builder().title(name + " nicht gefunden").cause(e).build();
		}
		return EventAssembler.toReferencelessDto(eventService.updateEvent(dto));
	}

	@PutMapping("/fields")
	@PreAuthorize(HAS_POTENTIALLY_ROLE_EVENT_MANAGE)
	public List<EventFieldDefaultDto> getEventFieldDefaults(@RequestBody String eventTypeName) {
		return getDefault(eventTypeName);
	}
}
