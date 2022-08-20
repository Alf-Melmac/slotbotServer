package de.webalf.slotbot.model.dtos.website.event.edit;

import de.webalf.slotbot.model.dtos.website.event.IdEntity;
import de.webalf.slotbot.model.dtos.website.event.creation.MinimalEventFieldDto;
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
public class MinimalEventFieldIdDto extends MinimalEventFieldDto implements IdEntity {
	long id;
}
