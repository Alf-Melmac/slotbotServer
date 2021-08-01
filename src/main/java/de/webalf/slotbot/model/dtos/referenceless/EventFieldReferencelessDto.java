package de.webalf.slotbot.model.dtos.referenceless;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static de.webalf.slotbot.util.MaxLength.EMBEDDABLE_TITLE;
import static de.webalf.slotbot.util.MaxLength.EMBEDDABLE_VALUE;

/**
 * @author Alf
 * @since 13.04.2021
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@Data
@SuperBuilder
public class EventFieldReferencelessDto extends AbstractIdEntityDto {
	@NotBlank
	@Size(max = EMBEDDABLE_TITLE)
	private String title;

	@NotBlank
	@Size(max = EMBEDDABLE_VALUE)
	private String text;

	private String link;
}
