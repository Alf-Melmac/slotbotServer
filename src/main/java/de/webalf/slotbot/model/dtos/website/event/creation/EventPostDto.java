package de.webalf.slotbot.model.dtos.website.event.creation;

import de.webalf.slotbot.model.dtos.website.event.EventActionDto;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Alf
 * @since 25.07.2022
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Value
@SuperBuilder
public class EventPostDto extends EventActionDto {
	List<MinimalEventFieldDto> details;

	List<MinimalSquadDto> squadList;
}
