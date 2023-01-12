package de.webalf.slotbot.exception;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Alf
 * @since 09.08.2020
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BusinessRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -3121618107868290626L;
	private String description;

	@Builder
	private BusinessRuntimeException(@NotEmpty String title, String description, Throwable cause) {
		super(title, cause);
		this.description = description;
	}
}
