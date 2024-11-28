package de.webalf.slotbot.feature.requirement.dto;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

/**
 * @author Alf
 * @since 18.11.2024
 */
@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
public class RequirementListDto extends AbstractIdEntityDto {
	@NotBlank
	@Size(max = TEXT)
	String name;

	List<RequirementDto> requirements;

	boolean memberAssignable;

	boolean enforced;
}
