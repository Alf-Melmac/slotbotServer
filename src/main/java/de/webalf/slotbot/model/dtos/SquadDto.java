package de.webalf.slotbot.model.dtos;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Alf
 * @since 23.06.2020
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SquadDto extends AbstractIdEntityDto {
	@Size(max = 80)
	private String name;

	private List<SlotDto> slotList;

	private EventDto event;

	@Builder
	public SquadDto(long id, String name, List<SlotDto> slotList, EventDto event) {
		this.id = id;
		this.name = name;
		this.slotList = slotList;
		this.event = event;
	}
}
