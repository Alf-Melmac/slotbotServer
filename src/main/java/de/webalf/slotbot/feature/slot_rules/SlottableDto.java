package de.webalf.slotbot.feature.slot_rules;

import de.webalf.slotbot.feature.requirement.dto.RequirementListDto;
import lombok.Builder;

import java.util.List;

/**
 * @author Alf
 * @since 15.01.2025
 */
@Builder
public record SlottableDto(SlottableState state, List<RequirementListDto> requirementsNotMet) {}
