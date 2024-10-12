package de.webalf.slotbot.assembler.website.event.edit;

import de.webalf.slotbot.assembler.minimal.MinimalEventFieldIdAssembler;
import de.webalf.slotbot.assembler.website.event.EventActionAssembler;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.website.event.edit.EventEditDto;
import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 20.08.2022
 */
@UtilityClass
public final class EventEditAssembler {
	public static EventEditDto toDto(Event event) {
		final EventEditDto.EventEditDtoBuilder<?, ?> builder = EventEditDto.builder();
		EventActionAssembler.toDto(builder, event);
		return builder
				.details(MinimalEventFieldIdAssembler.toDtoList(event.getDetails()))
				.squadList(MinimalSquadIdAssembler.toDtoList(event.getSquadList()))
				.ownerGuild(Long.toString(event.getOwnerGuild().getId()))
				.canRevokeShareable(event.canRevokeShareable())
				.canUploadSlotlist(event.isEmpty())
				.build();
	}
}
