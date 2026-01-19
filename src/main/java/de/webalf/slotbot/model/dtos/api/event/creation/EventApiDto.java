package de.webalf.slotbot.model.dtos.api.event.creation;

import de.webalf.slotbot.model.dtos.api.event.EventActionApiDto;
import de.webalf.slotbot.model.dtos.minimal.MinimalEventFieldDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Alf
 * @since 08.06.2023
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class EventApiDto extends EventActionApiDto {
	@Schema(defaultValue = "false", description = "Allows everyone interested to see the event in the calendar.")
	private final Boolean hidden;

	@Schema(defaultValue = "false", description = "Allows other groups to add this event ot their calendar and add participants through it.")
	private final Boolean shareable;

	@Schema(description = "Up to 22 additional details about an event.")
	private final List<MinimalEventFieldDto> details;

	private final List<SquadApiDto> squadList;
}
