package de.webalf.slotbot.exception;

import de.webalf.slotbot.model.annotations.ResponseView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Handles exceptions thrown in controllers annotated with {@link Controller}.
 * <p>
 * This translates exceptions to human-readable and valuable error messages for the caller.
 *
 * @author Alf
 * @since 09.06.2021
 */
@ControllerAdvice(annotations = Controller.class)
@Order(2)
@Slf4j
public class WebResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = {ResourceNotFoundException.class, MethodArgumentTypeMismatchException.class, ForbiddenException.class})
	protected RedirectView handleConflict(RuntimeException ex) {
		return new RedirectView(determineRedirectView(ex));
	}

	/**
	 * Uses the Annotation from the Exception if available
	 *
	 * @param e exception to check
	 * @return annotated reason or message of exception
	 */
	private String determineRedirectView(Exception e) {
		ResponseView responseViewAnnotation = AnnotationUtils.findAnnotation(e.getClass(), ResponseView.class);
		if (responseViewAnnotation != null) {
			return responseViewAnnotation.value();
		}
		try {
			return (String) ResponseView.class.getMethod("value").getDefaultValue();
		} catch (NoSuchMethodException noSuchMethodException) {
			log.error("Failed to get default value of ResponseView class", noSuchMethodException);
			return "";
		}
	}
}
