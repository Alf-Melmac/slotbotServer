package de.webalf.slotbot.exception;

import de.webalf.slotbot.model.annotations.ResponseView;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception commonly thrown in case a required permission is missing
 *
 * @author Alf
 * @since 18.08.2020
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
@ResponseView("/403")
public class ForbiddenException extends RuntimeException {
	public ForbiddenException(String message) {
		super(message);
	}

}
