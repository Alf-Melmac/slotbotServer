package de.webalf.slotbot.model.dtos;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Alf
 * @since 23.06.2020
 */
@MappedSuperclass
@Data
@RequiredArgsConstructor
@SuperBuilder
public abstract class AbstractIdEntityDto {
	@Id
	@NotNull
	protected long id;
}
