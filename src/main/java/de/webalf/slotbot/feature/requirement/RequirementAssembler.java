package de.webalf.slotbot.feature.requirement;

import de.webalf.slotbot.feature.requirement.dto.RequirementDto;
import de.webalf.slotbot.feature.requirement.dto.RequirementPostDto;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 15.11.2024
 */
@UtilityClass
final class RequirementAssembler {
	private static Requirement fromDto(@NonNull RequirementPostDto requirementPostDto) {
		return Requirement.builder()
				.name(requirementPostDto.name())
				.icon(requirementPostDto.icon())
				.build();
	}

	static List<Requirement> fromDtoList(Iterable<RequirementPostDto> dtos) {
		return StreamSupport.stream(dtos.spliterator(), false)
				.map(RequirementAssembler::fromDto)
				.toList();
	}

	private static RequirementDto toDto(@NonNull Requirement requirement) {
		return RequirementDto.builder()
				.id(requirement.getId())
				.name(requirement.getName())
				.icon(requirement.getIcon())
				.build();
	}

	static List<RequirementDto> toDtoList(Iterable<? extends Requirement> requirements) {
		return StreamSupport.stream(requirements.spliterator(), false)
				.map(RequirementAssembler::toDto)
				.toList();
	}
}
