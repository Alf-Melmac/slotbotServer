package de.webalf.slotbot.controller.api;

import de.webalf.slotbot.assembler.api.event.EventApiAssembler;
import de.webalf.slotbot.exception.ExceptionResponse;
import de.webalf.slotbot.model.annotations.springdoc.Resource;
import de.webalf.slotbot.model.dtos.api.event.view.EventApiIdDto;
import de.webalf.slotbot.service.api.EventApiService;
import de.webalf.slotbot.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static de.webalf.slotbot.configuration.springdoc.OpenApiConfig.SECURITY_KEY_WRITE;
import static de.webalf.slotbot.configuration.springdoc.TagNames.SLOTS;
import static de.webalf.slotbot.constant.Urls.API;
import static de.webalf.slotbot.util.ConstraintConstants.TEXT;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.HAS_POTENTIAL_WRITE_PERMISSION;

/**
 * @author Alf
 * @since 10.06.2023
 */
@RequestMapping(API + "/events/{id}/slot/{slotNumber}")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = SLOTS, description = "Everything related to slots in events")
@SecurityRequirement(name = SECURITY_KEY_WRITE)
public class SlottingApiController {
	private final EventApiService eventApiService;

	@PostMapping
	@PreAuthorize(HAS_POTENTIAL_WRITE_PERMISSION)
	@Operation(summary = "Slot or unslot a user in an event",
			responses = {
					@ApiResponse(responseCode = "200"),
					@ApiResponse(
							responseCode = "400",
							description = "Invalid parameters provided",
							content = @Content(
									schema = @Schema(implementation = ExceptionResponse.class),
									examples = {
											@ExampleObject(name = "Already slotted on this slot", value = """
													{
													    "errorMessage": "Die Person ist bereits auf diesem Slot",
													    "requestedURI": "/backend/slotbot/api/v1/events/1234/slot/1"
													}"""),
											@ExampleObject(name = "Slot is not empty", value = """
													{
													    "errorMessage": "Auf dem Slot befindet sich eine andere Person",
													    "requestedURI": "/backend/slotbot/api/v1/events/1234/slot/1"
													}"""),
											@ExampleObject(name = "Slot reserved for group to which user does not belong", value = """
													{
													    "errorMessage": "Dieser Slot ist für Mitglieder einer anderen Gruppe reserviert",
													    "requestedURI": "/backend/slotbot/api/v1/events/1234/slot/1"
													}"""),
											@ExampleObject(name = "Invalid user id - EN", value = """
													{
													     "errorMessage": "User id is not a valid discord id",
													     "requestedURI": "/backend/slotbot/api/v1/events/1234/slot/1"
													 }"""),
											@ExampleObject(name = "Invalid user id - DE", value = """
													{
													     "errorMessage": "User id ist keine gültige Discord-ID",
													     "requestedURI": "/backend/slotbot/api/v1/events/1234/slot/1"
													 }"""),
											@ExampleObject(name = "Unslot empty slot", value = """
													{
													     "errorMessage": "Einen leeren Slot brauchst du nicht ausslotten",
													     "requestedURI": "/backend/slotbot/api/v1/events/1234/slot/1"
													 }""")
									}))
			})
	@Resource
	public EventApiIdDto postSlot(@PathVariable(value = "id") long eventId,
	                              @PathVariable(value = "slotNumber") int slotNumber,
	                              @RequestBody(required = false) @Schema(format = "discord-snowflake", description = "Slot this user or, if empty, unslot this slot.") String userId) {
		if (StringUtils.isNotEmpty(userId)) {
			log.trace("postSlot: {} {} {}", eventId, slotNumber, userId);
			return EventApiAssembler.toDto(eventApiService.slot(eventId, slotNumber, userId));
		}

		log.trace("postUnslot: {} {}", eventId, slotNumber);
		return EventApiAssembler.toDto(eventApiService.unslot(eventId, slotNumber));
	}

	@PostMapping("/block")
	@PreAuthorize(HAS_POTENTIAL_WRITE_PERMISSION)
	@Operation(summary = "Block a slot in an event",
			description = "Blocks a slot from being slotted. If a block already exists, it can be used to overwrite the displayed text. If none is specified, the default is \"Gesperrt\".",
			responses = {
					@ApiResponse(responseCode = "200"),
					@ApiResponse(
							responseCode = "400",
							description = "Invalid parameters provided",
							content = @Content(
									schema = @Schema(implementation = ExceptionResponse.class),
									examples = {
											@ExampleObject(name = "Slot occupied by a user", value = """
													{
													    "errorMessage": "Der Slot ist belegt, die Person muss zuerst ausgeslottet werden.",
													    "requestedURI": "/backend/slotbot/api/v1/events/1234/slot/1/block"
													}"""),
											@ExampleObject(name = "No blocking allowed in reserve", value = """
													{
													    "errorMessage": "In der Reserve kann kein Slot blockiert werden.",
													    "requestedURI": "/backend/slotbot/api/v1/events/1234/slot/1/block"
													}""")
									}))
			})
	@Resource("/block")
	public EventApiIdDto postBlockSlot(@PathVariable(value = "id") long eventId,
	                                   @PathVariable(value = "slotNumber") int slotNumber,
	                                   @RequestBody(required = false) @Schema(maxLength = TEXT) String replacementText) {
		log.trace("postBlockSlot: {} {} {}", eventId, slotNumber, replacementText);
		return EventApiAssembler.toDto(eventApiService.blockSlot(eventId, slotNumber, replacementText));
	}
}
