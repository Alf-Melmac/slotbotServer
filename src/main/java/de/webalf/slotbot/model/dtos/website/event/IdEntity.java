package de.webalf.slotbot.model.dtos.website.event;

import jakarta.validation.constraints.NotNull;

/**
 * @author Alf
 * @since 20.08.2022
 */
public interface IdEntity {
	@NotNull
	long getId();
}
