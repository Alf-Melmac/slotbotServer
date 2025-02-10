package de.webalf.slotbot.feature.requirement;

import de.webalf.slotbot.feature.requirement.dto.ManagedRequirementDto;
import de.webalf.slotbot.feature.requirement.dto.ManagedRequirementListDto;
import de.webalf.slotbot.feature.requirement.model.Requirement;
import de.webalf.slotbot.feature.requirement.model.RequirementList;
import de.webalf.slotbot.model.User;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 09.02.2025
 */
@UtilityClass
class ManagedRequirementListAssembler {
	private static ManagedRequirementListDto toDto(@NonNull RequirementList requirementList, @NonNull User user) {
		return ManagedRequirementListDto.builder()
				.id(requirementList.getId())
				.name(requirementList.getName())
				.requirements(toRequirementDtoList(requirementList.getRequirements(), user))
				.build();
	}

	static List<ManagedRequirementListDto> toDtoList(Iterable<? extends RequirementList> requirements, @NonNull User user) {
		return StreamSupport.stream(requirements.spliterator(), false)
				.map(requirementList -> toDto(requirementList, user))
				.toList();
	}

	private static ManagedRequirementDto toDto(@NonNull Requirement requirement, @NonNull User user) {
		return RequirementAssembler.toDto(ManagedRequirementDto.builder(), requirement)
				.fulfilled(user.fulfillsRequirement(requirement))
				.build();
	}

	private static List<ManagedRequirementDto> toRequirementDtoList(Iterable<? extends Requirement> requirements, @NonNull User user) {
		return StreamSupport.stream(requirements.spliterator(), false)
				.map(requirement -> toDto(requirement, user))
				.toList();
	}
}
