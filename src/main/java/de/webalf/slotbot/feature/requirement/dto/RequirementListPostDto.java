package de.webalf.slotbot.feature.requirement.dto;

import de.webalf.slotbot.feature.requirement.RequirementList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

/**
 * DTO for {@link RequirementList}
 *
 * @author Alf
 * @since 15.11.2024
 */
public record RequirementListPostDto(Long id,
                                     @Size(max = TEXT) @NotBlank String name,
                                     List<RequirementPostDto> requirements,
                                     boolean memberAssignable,
                                     boolean enforced) {}
