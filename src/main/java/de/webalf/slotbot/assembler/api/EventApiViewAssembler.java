package de.webalf.slotbot.assembler.api;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.api.EventApiViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static de.webalf.slotbot.util.EventUtils.buildUrl;

/**
 * @author Alf
 * @since 08.06.2023
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public final class EventApiViewAssembler {
	private final SquadApiViewAssembler squadApiAssembler;

	public EventApiViewDto toViewDto(Event event) {
		return EventApiViewDto.builder()
				.id(event.getId())
				.name(event.getName())
				.dateTime(event.getDateTime())
				.squadList(squadApiAssembler.toViewDtoList(event.getSquadList()))
				.url(buildUrl(event))
				.build();
	}
}
