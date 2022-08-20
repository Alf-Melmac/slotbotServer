package de.webalf.slotbot.model.dtos.website.event.edit;

import de.webalf.slotbot.model.dtos.website.event.EventActionDto;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Alf
 * @since 20.08.2022
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class EventEditDto extends EventActionDto {
	List<MinimalEventFieldIdDto> details;

	List<MinimalSquadIdDto> squadList;
}
