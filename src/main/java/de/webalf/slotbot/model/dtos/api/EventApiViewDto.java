package de.webalf.slotbot.model.dtos.api;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

import static de.webalf.slotbot.util.MaxLength.TEXT;

/**
 * Event Dto that includes important fields needed to display a slotlist
 *
 * @author Alf
 * @since 22.02.2021
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class EventApiViewDto extends AbstractIdEntityDto {
	@NotBlank
	@Size(max = TEXT)
	String name;

	@NotNull
	LocalDateTime dateTime;

	List<SquadApiViewDto> squadList;

	@NotBlank
	String url;
}

