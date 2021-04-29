package de.webalf.slotbot.model.dtos;

import de.webalf.slotbot.model.enums.EventFieldType;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author Alf
 * @since 27.04.2021
 */
@Value
@Builder
public class EventFieldDefaultDto {
	@NotBlank
	String title;

	@NonNull
	EventFieldType type;

	List<String> selection;
}