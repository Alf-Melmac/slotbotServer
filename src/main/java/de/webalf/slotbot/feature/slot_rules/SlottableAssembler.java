package de.webalf.slotbot.feature.slot_rules;

import de.webalf.slotbot.feature.requirement.RequirementListAssembler;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 15.01.2025
 */
@UtilityClass
public class SlottableAssembler {
	public static SlottableDto toDto(@NonNull Slottable slottable) {
		return SlottableDto.builder()
				.state(slottable.state())
				.requirementsNotMet(RequirementListAssembler.toDtoList(slottable.requirementsNotMet()))
				.build();
	}
}
