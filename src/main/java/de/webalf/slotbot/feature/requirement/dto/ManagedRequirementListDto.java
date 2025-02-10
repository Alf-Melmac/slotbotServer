package de.webalf.slotbot.feature.requirement.dto;

import de.webalf.slotbot.feature.requirement.model.RequirementList;
import lombok.Builder;

import java.util.List;

/**
 * Representation of a {@link RequirementList#isMemberAssignable() non member assignable requirement list} for a user
 *
 * @author Alf
 * @since 09.02.2025
 */
@Builder
public record ManagedRequirementListDto(long id, String name, List<ManagedRequirementDto> requirements) {}
