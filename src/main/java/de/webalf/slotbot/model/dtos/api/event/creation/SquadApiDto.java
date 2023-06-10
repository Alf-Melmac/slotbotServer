package de.webalf.slotbot.model.dtos.api.event.creation;

import de.webalf.slotbot.model.dtos.api.event.AbstractSquadApiDto;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Alf
 * @since 14.04.2021
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@SuperBuilder
public class SquadApiDto extends AbstractSquadApiDto {
	private List<SlotApiDto> slotList;
}
