package de.webalf.slotbot.model.dtos;

import de.webalf.slotbot.model.enums.EventFieldType;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author Alf
 * @since 27.04.2021
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class EventFieldDefaultDto extends AbstractIdEntityDto {
	@NotBlank
	String title;

	@NonNull
	EventFieldType type;

	List<String> selection;

	String text;
}