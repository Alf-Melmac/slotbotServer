package de.webalf.slotbot.feature.slot_rules;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Alf
 * @since 12.01.2025
 */
@AllArgsConstructor
@Getter
public enum SlottableState {
	YES(true, null),
	YES_OWN(true, "slottableState.yes.own"),
	YES_REQUIREMENTS_NOT_MET(true, "slottableState.yes.requirementsNotMet"),
	NO(false, "slottableState.no"),
	NO_BLOCKED(false, "slottableState.no.blocked"),
	NO_RESERVED(false, "slottableState.no.reserved"),
	NO_REQUIREMENTS_NOT_MET(false, "slottableState.no.requirementsNotMet"),
	NO_BANNED(false, "slottableState.no.banned"),
	NOT_AVAILABLE(false, "slottableState.no.notAvailable");

	private final boolean isSlottingAllowed;
	private final String messageKey;
}
