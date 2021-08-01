package de.webalf.slotbot.model.dtos;

import de.webalf.slotbot.model.enums.EventFieldType;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

import static de.webalf.slotbot.util.MaxLength.EMBEDDABLE_TITLE;
import static de.webalf.slotbot.util.MaxLength.EMBEDDABLE_VALUE;

/**
 * @author Alf
 * @since 27.04.2021
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class EventFieldDefaultDto extends AbstractIdEntityDto {
	@NotBlank
	@Size(max = EMBEDDABLE_TITLE)
	String title;

	@NonNull
	EventFieldType type;

	List<String> selection;

	@Size(max = EMBEDDABLE_VALUE)
	String text;
}