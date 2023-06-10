package de.webalf.slotbot.model.dtos;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 23.06.2020
 */
@Data
@RequiredArgsConstructor
@SuperBuilder
public abstract class AbstractIdEntityDto {
	protected long id;
}
