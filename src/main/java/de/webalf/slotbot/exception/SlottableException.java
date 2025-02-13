package de.webalf.slotbot.exception;

import de.webalf.slotbot.feature.slot_rules.Slottable;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a slotting request is denied
 *
 * @author Alf
 * @since 13.02.2025
 */
@Getter
@Builder
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SlottableException extends RuntimeException {
	/**
	 * Reason of denial
	 */
	private final Slottable slottable;
}
