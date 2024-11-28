package de.webalf.slotbot.feature.requirement.dto;

import de.webalf.slotbot.feature.requirement.Requirement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;
import static de.webalf.slotbot.util.ConstraintConstants.URL;

/**
 * DTO for {@link Requirement}
 *
 * @author Alf
 * @since 15.11.2024
 */
public record RequirementPostDto(Long id,
                                 @Size(max = TEXT) @NotBlank String name,
                                 @Size(max = URL) String icon) {}
