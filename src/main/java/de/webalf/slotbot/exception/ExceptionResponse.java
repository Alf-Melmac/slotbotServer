package de.webalf.slotbot.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Alf
 * @since 09.08.2020
 */
@Getter
@Setter
@Builder
public class ExceptionResponse {
	private String errorMessage;
	private String requestedURI;
}
