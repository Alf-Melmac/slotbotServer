package de.webalf.slotbot.exception;

import de.webalf.slotbot.util.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles exceptions thrown in controllers annotated with {@link RestController}.
 * <p>
 * This translates exceptions to human-readable and valuable error messages for the caller.
 *
 * @author Alf
 * @since 09.08.2020
 */
@ControllerAdvice(annotations = RestController.class)
@Order(1)
class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = {ResourceNotFoundException.class, BusinessRuntimeException.class, ForbiddenException.class,
			BadCredentialsException.class})
	protected ResponseEntity<?> handleConflict(RuntimeException ex, HttpServletRequest request) {
		return new ResponseEntity<>(
				ExceptionResponse.builder()
						.errorMessage(determineErrorMessage(ex))
						.requestedURI(request.getRequestURI())
						.build(),
				determineHttpStatus(ex));
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		final String errorMessage = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(", "));

		return new ResponseEntity<>(
				ExceptionResponse.builder()
						.errorMessage(errorMessage + (fieldErrors.size() > 1 ? " are invalid." : errorMessage + " is invalid.") + " Missing mandatory field?")
						.requestedURI(((ServletWebRequest) request).getRequest().getRequestURI())
						.build(),
				HttpStatus.BAD_REQUEST);
	}

	/**
	 * Uses the Annotation from the Exception if available
	 *
	 * @param e exception to check
	 * @return annotated reason or message of exception
	 */
	private String determineErrorMessage(Exception e) {
		ResponseStatus responseStatusAnnotation = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);
		if (responseStatusAnnotation != null) {
			String reason = responseStatusAnnotation.reason();
			if (StringUtils.isNotEmpty(reason)) {
				return responseStatusAnnotation.reason();
			}
		}
		return e.getMessage();
	}

	/**
	 * Uses the Annotation from the Exception if available
	 *
	 * @param e exception to check
	 * @return annotated Status or HttpStatus.INTERNAL_SERVER_ERROR
	 */
	static HttpStatus determineHttpStatus(Exception e) {
		if (e instanceof BadCredentialsException) {
			return HttpStatus.UNAUTHORIZED;
		}

		ResponseStatus responseStatusAnnotation = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);
		if (responseStatusAnnotation != null) {
			return responseStatusAnnotation.value();
		}
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}
}
