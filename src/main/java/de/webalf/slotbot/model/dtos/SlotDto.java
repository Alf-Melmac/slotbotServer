package de.webalf.slotbot.model.dtos;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;

/**
 * @author Alf
 * @since 23.06.2020
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@SuperBuilder
public class SlotDto extends AbstractIdEntityDto {
	@Size(max = 80)
	private final String name;

	private final int number;

	private final SquadDto squad;

	private UserDto user;

	@Size(max = 80)
	private final String replacementText;
}
