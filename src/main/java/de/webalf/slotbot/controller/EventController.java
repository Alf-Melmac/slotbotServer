package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.website.CalendarEventAssembler;
import de.webalf.slotbot.assembler.website.EventDetailsAssembler;
import de.webalf.slotbot.assembler.website.event.creation.EventPostAssembler;
import de.webalf.slotbot.assembler.website.event.edit.EventEditAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.dtos.EventFieldDefaultDto;
import de.webalf.slotbot.model.dtos.website.CalendarEventDto;
import de.webalf.slotbot.model.dtos.website.EventDetailsDto;
import de.webalf.slotbot.model.dtos.website.event.creation.EventPostDto;
import de.webalf.slotbot.model.dtos.website.event.edit.EventEditDto;
import de.webalf.slotbot.model.dtos.website.event.edit.EventUpdateDto;
import de.webalf.slotbot.service.EventService;
import de.webalf.slotbot.service.GuildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static de.webalf.slotbot.util.eventfield.EventFieldUtils.getDefault;
import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_POTENTIALLY_ROLE_EVENT_MANAGE;
import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_ROLE_EVERYONE;

/**
 * @author Alf
 * @since 25.10.2020
 */
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventController {
	private final GuildService guildService;
	private final EventService eventService;
	private final EventDetailsAssembler eventDetailsAssembler;

	@GetMapping(value = "/list")
	public List<CalendarEventDto> getBetween(@RequestParam LocalDateTime start,
	                                         @RequestParam LocalDateTime end) {
		return CalendarEventAssembler.toDtoList(eventService.findAllBetweenOfGuild(start, end, guildService.findCurrentNonNullGuild()));
	}

	@GetMapping("/{id}/details")
	public EventDetailsDto getEventDetails(@PathVariable(value = "id") long eventId) {
		return eventDetailsAssembler.toDto(eventService.findById(eventId));
	}

	@PostMapping
	@PreAuthorize("@permissionChecker.hasEventManagePermissionInCurrentOwnerGuild()")
	public long postEvent(@Valid @RequestBody EventPostDto event) {
		return eventService.createEvent(event).getId();
	}

	@GetMapping("/{id}/copy")
	@PreAuthorize("@permissionChecker.hasEventManagePermission(#eventId)")
	public EventPostDto getEventForCopy(@PathVariable(value = "id") long eventId) {
		return EventPostAssembler.toDto(eventService.findById(eventId));
	}

	@GetMapping("/{id}/edit")
	@PreAuthorize("@permissionChecker.hasEventManagePermission(#eventId)")
	public EventEditDto getEventForEdit(@PathVariable(value = "id") long eventId) {
		return EventEditAssembler.toDto(eventService.findById(eventId));
	}

	@PutMapping("/{id}")
	@PreAuthorize("@permissionChecker.hasEventManagePermission(#eventId)")
	public EventEditDto updateEvent(@PathVariable(value = "id") long eventId, @RequestBody EventUpdateDto event) {
		return EventEditAssembler.toDto(eventService.updateEvent(eventId, event));
	}

	@PutMapping("/{id}/edit/text")
	@PreAuthorize("@permissionChecker.hasEventManagePermission(#eventId)")
	public EventEditDto updateEventField(@PathVariable(value = "id") long eventId, @RequestBody Map.Entry<String, String> field) {
		final String name = field.getKey();
		EventUpdateDto dto = EventUpdateDto.builder().build();
		try {
			ReflectionUtils.setField(dto.getClass().getSuperclass().getDeclaredField(name), dto, field.getValue().trim());
		} catch (NoSuchFieldException e) {
			log.error("Can't find field " + name + " while trying to edit it.", e);
			throw BusinessRuntimeException.builder().title(name + " nicht gefunden").cause(e).build();
		} catch (NullPointerException e) {
			throw BusinessRuntimeException.builder().title(name + " darf nicht leer sein").cause(e).build();
		}
		return EventEditAssembler.toDto(eventService.updateEvent(eventId, dto));
	}

	@GetMapping ("/fields")
	@PreAuthorize(HAS_POTENTIALLY_ROLE_EVENT_MANAGE)
	public List<EventFieldDefaultDto> getEventFieldDefaults(@RequestParam String eventTypeName) {
		return getDefault(eventTypeName);
	}

	@PutMapping("/slotting/{id}")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public EventDetailsDto putSlotting(@PathVariable(value = "id") long slotId) {
		return eventDetailsAssembler.toDto(eventService.slot(slotId));
	}

	@PutMapping("/unslotting/{id}")
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public EventDetailsDto putUnslotting(@PathVariable(value = "id") long slotId) {
		return eventDetailsAssembler.toDto(eventService.unslot(slotId));
	}
}
