package de.webalf.slotbot.model.dtos.api.event.view;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.webalf.slotbot.model.dtos.api.event.AbstractSquadApiDto;
import de.webalf.slotbot.model.dtos.minimal.IdEntity;
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
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@SuperBuilder
//Just for the readability
@JsonPropertyOrder({"id", "name", "reservedForGuildId", "slotList"})
public class SquadApiIdDto extends AbstractSquadApiDto implements IdEntity {
	private long id;

	private List<SlotApiIdDto> slotList;
}
