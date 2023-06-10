package de.webalf.slotbot.model.dtos.api.event.view;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.webalf.slotbot.model.dtos.api.event.creation.SlotApiDto;
import de.webalf.slotbot.model.dtos.minimal.IdEntity;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 08.06.2023
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@SuperBuilder
//Just for the readability
@JsonPropertyOrder({"id", "name", "number", "reservedForGuildId", "userId", "replacementText"})
public class SlotApiIdDto extends SlotApiDto implements IdEntity {
	private long id;
}
