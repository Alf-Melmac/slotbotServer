package de.webalf.slotbot.feature.requirement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

/**
 * @author Alf
 * @since 01.12.2024
 */
@Builder
public record EventTypeRequirementListDto(long id,
                                          @NotBlank @Size(max = TEXT) String name,
										  List<RequirementDto> requirements,
                                          boolean active) {}
