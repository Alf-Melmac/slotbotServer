package de.webalf.slotbot.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static de.webalf.slotbot.exception.RestResponseEntityExceptionHandler.determineHttpStatus;

/**
 * Handles exceptions matched by {@link RestControllerAdvice}.
 * <p>
 * This translates exceptions to human-readable and valuable error messages for the caller.
 *
 * @author Alf
 * @since 05.10.2021
 */
@RestControllerAdvice
@Order(3)
public class OtherResponseEntityExceptionHandler {
	@ExceptionHandler(ForbiddenException.class)
	protected ResponseEntity<ExceptionResponse> handleConflict(RuntimeException ex, HttpServletRequest request) {
		return new ResponseEntity<>(
				ExceptionResponse.builder()
						.errorMessage(ex.getMessage())
						.requestedURI(request.getRequestURI())
						.build(),
				determineHttpStatus(ex));
	}
}
