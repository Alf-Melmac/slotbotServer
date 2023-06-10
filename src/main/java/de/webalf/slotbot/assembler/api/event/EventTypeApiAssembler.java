package de.webalf.slotbot.assembler.api.event;

import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.dtos.api.event.EventTypeApiDto;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 08.06.2023
 */
@UtilityClass
public final class EventTypeApiAssembler {
	public static EventType fromDto(@NonNull EventTypeApiDto dto, Guild guild) {
		return EventType.builder()
				.name(dto.getName())
				.color(dto.getColor())
				.guild(guild)
				.build();
	}

	static EventTypeApiDto toDto(@NonNull EventType eventType) {
		return EventTypeApiDto.builder()
				.name(eventType.getName())
				.color(eventType.getColor())
				.build();
	}
}
