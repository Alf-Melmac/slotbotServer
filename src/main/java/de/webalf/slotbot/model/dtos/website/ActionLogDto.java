package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.model.enums.LogAction;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import static de.webalf.slotbot.model.enums.LogAction.*;

/**
 * @author Alf
 * @since 20.11.2020
 */
@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@Setter
public class ActionLogDto extends RepresentationModel<ActionLogDto> {
	private final UserNameDto user;

	private String objectName;
	private String objectUrl;

	private final LogAction action;

	private final String timeLeft;

	public boolean isEventAction() {
		return action == SLOT || action == UNSLOT || action == SWAP;
	}
}
