package de.webalf.slotbot.model.dtos;

import lombok.Getter;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

/**
 * @author Alf
 * @since 23.06.2020
 */
@MappedSuperclass
@Getter
public class AbstractIdEntityDto {
	@Id
	@NotNull
	protected long id;
}
