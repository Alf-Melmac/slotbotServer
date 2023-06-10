package de.webalf.slotbot.model.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * @author Alf
 * @since 04.07.2021
 */
@Data
@Builder
public class EventDiscordInformationDto {
	//String is needed, because the discord snowflakes exceed the maximum size of a JavaScript number. See https://stackoverflow.com/questions/1379934/large-numbers-erroneously-rounded-in-javascript
	@NotBlank
	private final String channel;

	@NotBlank
	private final String guild;

	private String infoMsg;

	private String slotListMsgPartOne;

	private String slotListMsgPartTwo;
}
