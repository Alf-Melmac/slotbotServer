package de.webalf.slotbot.model.dtos;

import lombok.Builder;
import lombok.Data;

/**
 * @author Alf
 * @since 04.07.2021
 */
@Data
@Builder
public class EventDiscordInformationDto {
	private final long channel;

	private final long guild;

	private Long infoMsg;

	private Long slotListMsgPartOne;

	private Long slotListMsgPartTwo;

	private Long scheduledEvent;
}
