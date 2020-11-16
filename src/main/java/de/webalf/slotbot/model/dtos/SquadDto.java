package de.webalf.slotbot.model.dtos;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Alf
 * @since 23.06.2020
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Value
@SuperBuilder
public class SquadDto extends AbstractIdEntityDto {
	@Size(max = 80)
	String name;

	List<SlotDto> slotList;

	EventDto event;
}
