package de.webalf.slotbot.controller.api;

import de.webalf.slotbot.assembler.api.event.EventApiAssembler;
import de.webalf.slotbot.exception.ExceptionResponse;
import de.webalf.slotbot.model.annotations.springdoc.Resource;
import de.webalf.slotbot.model.dtos.api.event.creation.EventApiDto;
import de.webalf.slotbot.model.dtos.api.event.view.EventApiIdDto;
import de.webalf.slotbot.service.api.EventApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static de.webalf.slotbot.configuration.springdoc.OpenApiConfig.*;
import static de.webalf.slotbot.configuration.springdoc.TagNames.EVENTS;
import static de.webalf.slotbot.constant.Urls.API;
import static de.webalf.slotbot.util.DateUtils.atEndOfDay;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.HAS_ADMIN_PERMISSION;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.HAS_POTENTIAL_READ_PUBLIC_PERMISSION;

/**
 * @author Alf
 * @since 22.06.2020
 */
@RequestMapping(API + "/events")
@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = EVENTS, description = "Everything related to events")
public class EventApiController {
	private final EventApiService eventApiService;

	@GetMapping("/{id}")
	@PreAuthorize(HAS_POTENTIAL_READ_PUBLIC_PERMISSION)
	@SecurityRequirement(name = SECURITY_KEY_READ_PUBLIC)
	@SecurityRequirement(name = SECURITY_KEY_READ)
	@Resource("/{id}")
	public EventApiIdDto getEvent(@PathVariable(value = "id") long eventId) {
		log.trace("getEvent: {}", eventId);
		return EventApiAssembler.toDto(eventApiService.findById(eventId));
	}

	@GetMapping
	@PreAuthorize(HAS_POTENTIAL_READ_PUBLIC_PERMISSION)
	@Operation(summary = "All events between two dates",
			description = "Search for events in a specific time frame of up to 30 days.",
			responses = {
					@ApiResponse(responseCode = "200"),
					@ApiResponse(
							responseCode = "400",
							description = "Invalid parameters provided",
							content = @Content(
									schema = @Schema(implementation = ExceptionResponse.class),
									examples = {
											@ExampleObject(name = "Request exceeds maximum of 30 days", value = """
													{
													    "errorMessage": "Request exceeds maximum of 30 days. You have entered 42.",
													    "requestedURI": "/backend/slotbot/api/v1/events"
													}"""),
											@ExampleObject(name = "Invalid format", value = """
													{
													    "errorMessage": "Failed to convert 'end' with value: 'not-a-date'",
													    "requestedURI": "/backend/slotbot/api/v1/events"
													}""")
									}))
			})
	@SecurityRequirement(name = SECURITY_KEY_READ_PUBLIC)
	@SecurityRequirement(name = SECURITY_KEY_READ)
	public List<EventApiIdDto> getEvents(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Schema(format = "yyyy-MM-dd", description = "Starting with the date in UTC including the date") LocalDate start,
	                                     @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Schema(format = "yyyy-MM-dd", description = "Ending with the date in UTC including the date") LocalDate end) {
		log.trace("getEvents: {} - {}", start, end);
		return EventApiAssembler.toDtoList(eventApiService.findAllBetween(start.atStartOfDay(), atEndOfDay(end)));
	}

	@PostMapping
	@PreAuthorize("@apiPermissionChecker.assertApiWriteAccess()")
	@Operation(summary = "Create an event",
			responses = {
					@ApiResponse(responseCode = "200"),
					@ApiResponse(
							responseCode = "400",
							description = "Invalid parameters provided",
							content = @Content(
									schema = @Schema(implementation = ExceptionResponse.class),
									examples = {
											@ExampleObject(name = "Missing required field or invalid pattern", value = """
													{
													    "errorMessage": "creator is invalid. Missing mandatory field?",
													    "requestedURI": "/backend/slotbot/api/v1/events/1234"
													}"""),
											@ExampleObject(name = "Missing required fields or invalid patterns", value = """
													{
													    "errorMessage": "eventType, creator are invalid. Missing mandatory fields?",
													    "requestedURI": "/backend/slotbot/api/v1/events/1234"
													}"""),
											@ExampleObject(name = "Duplicated slot number", value = """
													{
													    "errorMessage": "Slotnummern müssen innerhalb eines Events eindeutig sein.",
													    "requestedURI": "/backend/slotbot/api/v1/events/1234"
													}"""),
											@ExampleObject(name = "Too many detail fields", value = """
													{
													    "errorMessage": "Es dürfen nur 23 Detailfelder angegeben werden.",
													    "requestedURI": "/backend/slotbot/api/v1/events/1234"
													}""")
									}))
			})
	@SecurityRequirement(name = SECURITY_KEY_WRITE)
	public EventApiIdDto postEvent(@Valid @RequestBody EventApiDto event) {
		log.trace("postEvent: {}", event.getName());
		return EventApiAssembler.toDto(eventApiService.create(event));
	}

	/*@PutMapping("/{id}")
	@PreAuthorize(HAS_POTENTIAL_WRITE_PERMISSION)
	@SecurityRequirement(name = SECURITY_KEY_WRITE)
	@Resource("/{id}")
	public EventApiIdDto updateEvent(@PathVariable(value = "id") long eventId, @RequestBody EventApiDto event) {
		log.trace("updateEvent: {}", event.getName());
		return EventApiAssembler.toDto(eventApiService.update(eventId, event));
	}*/

	@DeleteMapping("/{id}")
	@PreAuthorize(HAS_ADMIN_PERMISSION)
	@SecurityRequirement(name = SECURITY_KEY_ADMIN)
	@Resource("/{id}")
	public void deleteEvent(@PathVariable(value = "id") long eventId) {
		log.trace("deleteEvent: {}", eventId);
		eventApiService.delete(eventId);
	}
}
