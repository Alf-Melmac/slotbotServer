package de.webalf.slotbot.model.dtos.website.event.edit;

import de.webalf.slotbot.model.dtos.website.event.IdEntity;
import de.webalf.slotbot.model.dtos.website.event.creation.MinimalEventFieldDto;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 20.08.2022
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@SuperBuilder
public class MinimalEventFieldIdDto extends MinimalEventFieldDto implements IdEntity {
	private long id;
}
