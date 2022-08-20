package de.webalf.slotbot.model.dtos.website.event.edit;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

import static de.webalf.slotbot.util.MaxLength.TEXT;

/**
 * @author Alf
 * @since 20.08.2022
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class MinimalSquadIdDto extends AbstractIdEntityDto {
	@NotBlank
	@Size(max = TEXT)
	String name;

	List<MinimalSlotIdDto> slotList;

	String reservedFor;
}
