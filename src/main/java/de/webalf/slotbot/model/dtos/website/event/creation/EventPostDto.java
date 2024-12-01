package de.webalf.slotbot.model.dtos.website.event.creation;

import de.webalf.slotbot.model.dtos.minimal.MinimalEventFieldDto;
import de.webalf.slotbot.model.dtos.website.event.EventActionDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Alf
 * @since 25.07.2022
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE) //Allow jackson to serialize this class
@Value
@SuperBuilder
public class EventPostDto extends EventActionDto {
	List<MinimalEventFieldDto> details;

	List<MinimalSquadDto> squadList;
}
