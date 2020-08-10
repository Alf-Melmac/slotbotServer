package de.webalf.slotbot.model.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Alf
 * @since 23.06.2020
 */
@RequiredArgsConstructor
@Getter
public class SquadDto extends AbstractIdEntityDto {
	@Size(max = 80)
	private String name;

	private List<SlotDto> slotList;

	private EventDto event;

	@Builder
	public SquadDto(final long id, final String name, final List<SlotDto> slotList, EventDto event) {
		this.id = id;
		this.name = name;
		this.slotList = slotList;
		this.event = event;
	}
}
