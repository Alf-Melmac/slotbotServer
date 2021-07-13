package de.webalf.slotbot.model.dtos;

import de.webalf.slotbot.util.DateUtils;
import de.webalf.slotbot.util.ListUtils;
import de.webalf.slotbot.util.StringUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

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

	@Size(max = MessageEmbed.DESCRIPTION_MAX_LENGTH)
	private String description;

	@Size(max = 1666)
	private String pictureUrl;

	@Size(max = 80)
	private String missionType;

	@Size(max = 80)
	private String missionLength;

	private Boolean reserveParticipating;

	private EventDiscordInformationDto discordInformation;

	private static final String AMB_LOGO = "https://cdn.discordapp.com/attachments/759147249325572097/759147455483740191/AM-Blau-big-bananemitschokokuchen.jpg";
	public String getPictureUrl() {
		return StringUtils.isNotEmpty(pictureUrl) ? pictureUrl : AMB_LOGO;
	}

	public String getRawPictureUrl() {
		return pictureUrl;
	}

	@Getter
	private static final List<String> MISSION_TYPES = List.of("COOP", "COOP+", "Zeus", "TvT", "Training", "Spezial", "Anderes");

	/**
	 * Returns known mission types, except the one saved in the event
	 *
	 * @return known mission types except the saved one
	 */
	@SuppressWarnings("unused") //eventEdit.html
	public List<String> getMissionTypesFiltered() {
		return ListUtils.getListFiltered(MISSION_TYPES, getMissionType());
	}

	public ZonedDateTime getDateTimeZoned() {
		return DateUtils.getDateTimeZoned(date, startTime);
	}
}
