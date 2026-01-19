package de.webalf.slotbot.model.dtos.api.event.view;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.webalf.slotbot.model.dtos.api.event.EventActionApiDto;
import de.webalf.slotbot.model.dtos.minimal.IdEntity;
import de.webalf.slotbot.model.dtos.minimal.MinimalEventFieldIdDto;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Alf
 * @since 04.11.2020
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
//Just for the readability
@JsonPropertyOrder({"id", "hidden", "name", "dateTime", "creator", "eventType", "description", "missionType", "missionLength", "pictureUrl", "details", "reserveParticipating", "squadList"})
public class EventApiIdDto extends EventActionApiDto implements IdEntity {
	long id;

	boolean hidden;

	@Size(max = 22)
	List<MinimalEventFieldIdDto> details;

	List<SquadApiIdDto> squadList;
}
