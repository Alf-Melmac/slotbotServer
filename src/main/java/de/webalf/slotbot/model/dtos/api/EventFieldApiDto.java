package de.webalf.slotbot.model.dtos.api;

import de.webalf.slotbot.model.dtos.referenceless.EventFieldReferencelessDto;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 14.04.2021
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class EventFieldApiDto extends EventFieldReferencelessDto {
}
