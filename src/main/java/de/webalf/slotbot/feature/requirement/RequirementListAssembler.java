package de.webalf.slotbot.feature.requirement;

import de.webalf.slotbot.feature.requirement.dto.RequirementListDto;
import de.webalf.slotbot.feature.requirement.dto.RequirementListPostDto;
import de.webalf.slotbot.feature.requirement.model.Requirement;
import de.webalf.slotbot.feature.requirement.model.RequirementList;
import de.webalf.slotbot.model.Guild;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 15.11.2024
 */
@UtilityClass
public final class RequirementListAssembler {
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

	static RequirementListDto toDto(@NonNull RequirementList requirementList) {
		return toDto(requirementList, requirementList.getRequirements());
	}

	public static RequirementListDto toDto(@NonNull RequirementList requirementList, List<Requirement> overrideRequirements) {
		return RequirementListDto.builder()
				.id(requirementList.getId())
				.name(requirementList.getName())
				.requirements(RequirementAssembler.toDtoList(overrideRequirements))
				.memberAssignable(requirementList.isMemberAssignable())
				.enforced(requirementList.isEnforced())
				.global(requirementList.getGuild() == null)
				.build();
	}

	static List<RequirementListDto> toDtoList(Iterable<? extends RequirementList> requirements) {
		return StreamSupport.stream(requirements.spliterator(), false)
				.map(RequirementListAssembler::toDto)
				.toList();
	}

	public static List<RequirementListDto> toDtoList(Set<Requirement> requirements) {
		return requirements.stream()
				.collect(Collectors.groupingBy(Requirement::getRequirementList))
				.entrySet().stream()
				.sorted(Comparator.comparing(entry -> entry.getKey().getName()))
				.map(entry -> toDto(
						entry.getKey(),
						entry.getValue().stream().sorted(Comparator.comparing(Requirement::getName)).toList()
				))
				.toList();
	}
}
