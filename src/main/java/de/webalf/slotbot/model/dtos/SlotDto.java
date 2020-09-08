package de.webalf.slotbot.model.dtos;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Size;

/**
 * @author Alf
 * @since 23.06.2020
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SlotDto extends AbstractIdEntityDto {
	@Size(max = 80)
	private String name;

	private int number;

	private SquadDto squad;

	private UserDto user;

	@Builder
	public SlotDto(long id,
	               String name,
	               int number,
	               SquadDto squad,
	               UserDto user) {
		this.id = id;
		this.name = name;
		this.number = number;
		this.squad = squad;
		this.user = user;
	}

	public SlotDto slot(UserDto user) {
		this.user = user;
		return this;
	}
}
