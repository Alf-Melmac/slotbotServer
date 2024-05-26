package de.webalf.slotbot.model.dtos;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

/**
 * @author Alf
 * @since 23.06.2020
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Value
@SuperBuilder
public class SquadDto extends AbstractIdEntityDto {
	@Size(max = TEXT)
	String name;

	List<SlotDto> slotList;

	GuildDto reservedFor;
}
