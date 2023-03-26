package de.webalf.slotbot.model.dtos.website.event.edit;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 20.08.2022
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class EventEditDto extends EventUpdateDto {
	boolean canRevokeShareable;

	boolean canUploadSlotlist;
}
