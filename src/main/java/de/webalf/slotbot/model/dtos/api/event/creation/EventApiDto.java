package de.webalf.slotbot.model.dtos.api.event.creation;

import de.webalf.slotbot.model.dtos.api.event.EventActionApiDto;
import de.webalf.slotbot.model.dtos.minimal.MinimalEventFieldDto;
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
	private final Boolean hidden;

	private final Boolean shareable;

	private final List<MinimalEventFieldDto> details;

	private final List<SquadApiDto> squadList;
}
