package de.webalf.slotbot.model.dtos.website.event.edit;

import jakarta.validation.constraints.NotNull;
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
	@NotNull
	String ownerGuild;

	boolean canRevokeShareable;

	boolean canUploadSlotlist;
}
