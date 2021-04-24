package de.webalf.slotbot.model.dtos.api;

import de.webalf.slotbot.model.dtos.referenceless.EventReferencelessDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

/**
 * @author Alf
 * @since 04.11.2020
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class EventApiDto extends EventReferencelessDto {
	@NotBlank
	private String url;
}