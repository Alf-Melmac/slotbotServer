package de.webalf.slotbot.exception;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author Alf
 * @since 09.08.2020
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessRuntimeException extends RuntimeException {
	private String title;
	private String description;

	@Builder
	private BusinessRuntimeException(@NotEmpty String title, String description, Throwable cause) {
		super(title, cause);
		this.title = title;
		this.description = description;
	}
}
