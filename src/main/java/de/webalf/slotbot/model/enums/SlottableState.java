package de.webalf.slotbot.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Alf
 * @since 12.01.2025
 */
@AllArgsConstructor
@Getter
public enum SlottableState {
	YES(true),
	YES_OWN(true),
	YES_REQUIREMENTS_NOT_MET(true),
	NO(false),
	NO_BLOCKED(false),
	NO_RESERVED(false),
	NO_REQUIREMENTS_NOT_MET(false),
	NO_BANNED(false),
	NOT_AVAILABLE(false);

	private final boolean isSlottingAllowed;
}
