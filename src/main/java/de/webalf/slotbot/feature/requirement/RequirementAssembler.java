package de.webalf.slotbot.feature.requirement;

import de.webalf.slotbot.feature.requirement.dto.RequirementDto;
import de.webalf.slotbot.feature.requirement.dto.RequirementPostDto;
import de.webalf.slotbot.feature.requirement.model.Requirement;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

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

	static <C extends RequirementDto, B extends RequirementDto.RequirementDtoBuilder<C, B>> B
	toDto(RequirementDto.RequirementDtoBuilder<C, B> builder, @NotNull Requirement requirement) {
		return builder
				.id(requirement.getId())
				.name(requirement.getName())
				.abbreviation(requirement.getAbbreviation())
				.icon(requirement.getIcon());
	}

	private static RequirementDto toDto(@NonNull Requirement requirement) {
		return toDto(RequirementDto.builder(), requirement).build();
	}

	static List<RequirementDto> toDtoList(Iterable<? extends Requirement> requirements) {
		return StreamSupport.stream(requirements.spliterator(), false)
				.map(RequirementAssembler::toDto)
				.toList();
	}
}
