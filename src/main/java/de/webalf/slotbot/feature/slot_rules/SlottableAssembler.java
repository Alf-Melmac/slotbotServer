package de.webalf.slotbot.feature.slot_rules;

import de.webalf.slotbot.feature.requirement.RequirementListAssembler;
import de.webalf.slotbot.feature.requirement.dto.RequirementListDto;
import de.webalf.slotbot.feature.requirement.model.Requirement;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alf
 * @since 15.01.2025
 */
@UtilityClass
public class SlottableAssembler {
	public static SlottableDto toDto(@NonNull Slottable slottable) {
		final List<RequirementListDto> list = slottable.requirementsNotMet().stream()
				.collect(Collectors.groupingBy(Requirement::getRequirementList))
				.entrySet().stream()
				.map(entry -> RequirementListAssembler.toDto(entry.getKey(), entry.getValue()))
				.toList();

		return SlottableDto.builder()
				.state(slottable.state())
				.requirementsNotMet(list)
				.build();
	}
}
