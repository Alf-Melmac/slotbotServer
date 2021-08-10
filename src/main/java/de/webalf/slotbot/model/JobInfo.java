package de.webalf.slotbot.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Alf
 * @since 07.08.2021
 */
@Value
@Builder
public class JobInfo implements Serializable {
	private static final long serialVersionUID = -7375951716910560335L;
	@NotBlank
	String name;
	long recipient;
	@NonNull
	Date start;
	@NotBlank
	String message;
}
