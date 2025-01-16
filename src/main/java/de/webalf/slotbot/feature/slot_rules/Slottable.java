package de.webalf.slotbot.feature.slot_rules;

import de.webalf.slotbot.feature.requirement.model.Requirement;
import de.webalf.slotbot.model.enums.SlottableState;

import java.util.Set;

/**
 * @author Alf
 * @since 15.01.2025
 */
public record Slottable(SlottableState state, Set<Requirement> requirementsNotMet) {
	public Slottable(SlottableState state) {
		this(state, Set.of());
	}
}
