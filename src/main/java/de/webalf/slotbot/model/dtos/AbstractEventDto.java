package de.webalf.slotbot.model.dtos;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author Alf
 * @since 14.04.2021
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@SuperBuilder
public abstract class AbstractEventDto extends AbstractIdEntityDto {
	@NotNull
	private EventTypeDto eventType;

	@NotBlank
	@Size(max = 80)
	private String name;

	@NotNull
	private LocalDate date;

	@NotNull
	private LocalTime startTime;

	@NotBlank
	@Size(max = 80)
	private String creator;

	@Builder.Default
	private boolean hidden = false;

	//String is needed, because the discord IDs exceed the maximum size of a JavaScript number. See https://stackoverflow.com/questions/1379934/large-numbers-erroneously-rounded-in-javascript
	private String channel;

	private String infoMsg;

	private String slotListMsg;

	@Size(max = MessageEmbed.TEXT_MAX_LENGTH)
	private String description;

	@Size(max = 1666)
	private String pictureUrl;

	@Size(max = 80)
	private String missionType;

	@Size(max = 80)
	private String missionLength;

	private Boolean reserveParticipating;
}
