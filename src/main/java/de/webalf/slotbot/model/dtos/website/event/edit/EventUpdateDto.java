package de.webalf.slotbot.model.dtos.website.event.edit;

import de.webalf.slotbot.model.dtos.minimal.MinimalEventFieldIdDto;
import de.webalf.slotbot.model.dtos.website.event.EventActionDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Alf
 * @since 25.03.2023
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
public class EventUpdateDto extends EventActionDto {
	private List<MinimalEventFieldIdDto> details;

	private List<MinimalSquadIdDto> squadList;
}
