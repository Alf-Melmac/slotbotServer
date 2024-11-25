package de.webalf.slotbot.feature.requirement;

import de.webalf.slotbot.feature.requirement.dto.RequirementListDto;
import de.webalf.slotbot.feature.requirement.dto.RequirementListPostDto;
import de.webalf.slotbot.service.GuildService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 15.11.2024
 */
@Component
@RequiredArgsConstructor
class RequirementListAssembler {
	private final GuildService guildService;

	RequirementList fromDto(@NonNull RequirementListPostDto requirementList, long guildId) {
		final RequirementList list = RequirementList.builder()
				.guild(guildService.findExisting(guildId))
				.name(requirementList.name())
				.requirements(RequirementAssembler.fromDtoList(requirementList.requirements()))
				.memberAssignable(requirementList.memberAssignable())
				.enforced(requirementList.enforced())
				.build();
		list.setBackReferences();
		return list;
	}

	static RequirementListDto toDto(@NonNull RequirementList requirementList) {
		return RequirementListDto.builder()
				.id(requirementList.getId())
				.name(requirementList.getName())
				.requirements(RequirementAssembler.toDtoList(requirementList.getRequirements()))
				.memberAssignable(requirementList.isMemberAssignable())
				.enforced(requirementList.isEnforced())
				.build();
	}

	static List<RequirementListDto> toDtoList(Iterable<? extends RequirementList> requirements) {
		return StreamSupport.stream(requirements.spliterator(), false)
				.map(RequirementListAssembler::toDto)
				.toList();
	}
}
