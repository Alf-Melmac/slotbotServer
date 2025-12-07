package de.webalf.slotbot.exception;

import de.webalf.slotbot.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
	@ExceptionHandler(value = {ResourceNotFoundException.class, BusinessRuntimeException.class, ForbiddenException.class})
	protected ResponseEntity<ExceptionResponse> handleConflict(@NonNull RuntimeException ex, @NonNull HttpServletRequest request) {
		return ResponseEntity.status(determineHttpStatus(ex))
				.body(ExceptionResponse.builder()
						.errorMessage(determineErrorMessage(ex))
						.requestedURI(request.getRequestURI())
						.build());
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
		final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		final String errorMessage = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(", "));

		final boolean multiple = fieldErrors.size() > 1;
		return ResponseEntity.badRequest()
				.body(ExceptionResponse.builder()
						.errorMessage(errorMessage + (multiple ? " are" : " is") + " invalid. Missing mandatory field" + (multiple ? "s" : "") + "?")
						.requestedURI(((ServletWebRequest) request).getRequest().getRequestURI())
						.build());
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(@NonNull TypeMismatchException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
		return ResponseEntity.badRequest()
				.body(ExceptionResponse.builder()
						.errorMessage("Failed to convert '" + ex.getPropertyName() + "' with value: '" + ex.getValue() + "'")
						.requestedURI(((ServletWebRequest) request).getRequest().getRequestURI())
						.build());
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
		ResponseStatus responseStatusAnnotation = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);
		if (responseStatusAnnotation != null) {
			return responseStatusAnnotation.value();
		}
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}
}
