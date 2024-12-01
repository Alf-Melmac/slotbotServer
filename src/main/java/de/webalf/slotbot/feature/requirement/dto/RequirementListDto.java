package de.webalf.slotbot.feature.requirement.dto;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

/**
 * @author Alf
 * @since 18.11.2024
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class RequirementListDto extends AbstractIdEntityDto {
	@NotBlank
	@Size(max = TEXT)
	private final String name;

	private final List<RequirementDto> requirements;

	private final boolean memberAssignable;

	private final boolean enforced;
}
