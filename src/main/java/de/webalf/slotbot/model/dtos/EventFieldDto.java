package de.webalf.slotbot.model.dtos;

import de.webalf.slotbot.model.dtos.referenceless.EventFieldReferencelessDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 08.04.2021
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@SuperBuilder
public class EventFieldDto extends EventFieldReferencelessDto {
	private EventDto event;
}
