package de.webalf.slotbot.feature.requirement;

import de.webalf.slotbot.feature.requirement.dto.EventTypeRequirementListDto;
import de.webalf.slotbot.feature.requirement.model.RequirementList;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 12.12.2024
 */
@UtilityClass
final class EventTypeRequirementListAssembler {
	static EventTypeRequirementListDto toDto(@NonNull RequirementList requirementList, boolean active) {
		return EventTypeRequirementListDto.builder()
				.id(requirementList.getId())
				.name(requirementList.getName())
				.requirements(RequirementAssembler.toDtoList(requirementList.getRequirements()))
				.active(active)
				.build();
	}

	static List<EventTypeRequirementListDto> toDtoList(Iterable<? extends RequirementList> requirements) {
		return StreamSupport.stream(requirements.spliterator(), false)
				.map(requirementList -> toDto(requirementList, true))
				.toList();
	}
}
