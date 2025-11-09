package de.webalf.slotbot.feature.swap;

/**
 * @author Alf
 * @since 22.08.2023
 */
public enum SwapRequestResult {
	ERROR_OWN_SLOT,
	ERROR_PENDING,
	ERROR_NOT_AVAILABLE_FOR_REQUESTER,
	ERROR_NOT_AVAILABLE_FOR_FOREIGN,
	SUCCESS,
}
