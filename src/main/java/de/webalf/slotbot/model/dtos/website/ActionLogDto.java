package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.model.enums.LogAction;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

/**
 * @author Alf
 * @since 20.11.2020
 */
@Builder
@Data
@Setter
public class ActionLogDto {
	private final UserNameDto user;

	private String objectName;
	private String objectUrl;

	private final LogAction action;

	private final String timeLeft;
}
