package de.webalf.slotbot.model.dtos.website.event.edit;

import de.webalf.slotbot.model.dtos.website.event.IdEntity;
import de.webalf.slotbot.model.dtos.website.event.creation.MinimalSlotDto;
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
public class MinimalSlotIdDto extends MinimalSlotDto implements IdEntity {
	long id;
}
