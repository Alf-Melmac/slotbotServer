package de.webalf.slotbot.exception;

import de.webalf.slotbot.model.annotations.ResponseView;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception commonly thrown in case a required resource is missing.
 *
 * @author Alf
 * @since 22.06.2020
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
@ResponseView
public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException() {
		super();
	}

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
