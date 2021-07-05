package de.webalf.slotbot.model.dtos;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Alf
 * @since 04.07.2021
 */
@Data
@Builder
public class EventDiscordInformationDto {
	//String is needed, because the discord IDs exceed the maximum size of a JavaScript number. See https://stackoverflow.com/questions/1379934/large-numbers-erroneously-rounded-in-javascript
	@NotBlank
	private final String channel;

	private String infoMsg;

	private String slotListMsgPartOne;

	private String slotListMsgPartTwo;
}
