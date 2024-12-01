package de.webalf.slotbot.feature.requirement;

import de.webalf.slotbot.feature.requirement.dto.EventTypeRequirementListDto;
import de.webalf.slotbot.feature.requirement.dto.RequirementListDto;
import de.webalf.slotbot.feature.requirement.dto.RequirementListPostDto;
import de.webalf.slotbot.feature.requirement.model.RequirementList;
import de.webalf.slotbot.model.Guild;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 15.11.2024
 */
@UtilityClass
final class RequirementListAssembler {
	static RequirementList fromDto(@NonNull RequirementListPostDto requirementList, @NonNull Guild guild) {
		final RequirementList list = RequirementList.builder()
				.guild(guild)
				.name(requirementList.name())
				.requirements(RequirementAssembler.fromDtoList(requirementList.requirements()))
				.memberAssignable(requirementList.memberAssignable())
				.enforced(requirementList.enforced())
				.build();
		list.setBackReferences();
		return list;
	}

	private static <C extends RequirementListDto, B extends RequirementListDto.RequirementListDtoBuilder<C, B>> B toDto(RequirementListDto.RequirementListDtoBuilder<C, B> builder, @NonNull RequirementList requirementList) {
		return builder
				.id(requirementList.getId())
				.name(requirementList.getName())
				.requirements(RequirementAssembler.toDtoList(requirementList.getRequirements()))
				.memberAssignable(requirementList.isMemberAssignable())
				.enforced(requirementList.isEnforced());
	}

	static RequirementListDto toDto(@NonNull RequirementList requirementList) {
		return toDto(RequirementListDto.builder(), requirementList)
				.build();
	}

	static List<RequirementListDto> toDtoList(Iterable<? extends RequirementList> requirements) {
		return StreamSupport.stream(requirements.spliterator(), false)
				.map(RequirementListAssembler::toDto)
				.toList();
	}

	static EventTypeRequirementListDto toEventTypeDto(@NonNull RequirementList requirementList, boolean active) {
		return toDto(EventTypeRequirementListDto.builder(), requirementList)
				.active(active)
				.build();
	}
}
