package de.webalf.slotbot.model.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Size;

/**
 * @author Alf
 * @since 23.06.2020
 */
@RequiredArgsConstructor
@Getter
public class SlotDto extends AbstractIdEntityDto {
	@Size(max = 80)
	private String name;

	private int number;

	private SquadDto squad;

	//String is needed, because the discord IDs exceed the maximum size of a JavaScript number. See https://stackoverflow.com/questions/1379934/large-numbers-erroneously-rounded-in-javascript
	private String userId;

	@Builder
	public SlotDto(final long id,
	               final String name,
	               final int number,
	               final SquadDto squad,
	               final String userId) {
		this.id = id;
		this.name = name;
		this.number = number;
		this.squad = squad;
		this.userId = userId;
	}

	public SlotDto slot(String userId) {
		this.userId = userId;
		return this;
	}
}
