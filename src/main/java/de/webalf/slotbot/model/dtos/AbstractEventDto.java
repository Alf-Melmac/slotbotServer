package de.webalf.slotbot.model.dtos;

import de.webalf.slotbot.util.DateUtils;
import de.webalf.slotbot.util.GuildUtils;
import de.webalf.slotbot.util.ListUtils;
import de.webalf.slotbot.util.StringUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static de.webalf.slotbot.util.MaxLength.*;

/**
 * @author Alf
 * @since 14.04.2021
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@SuperBuilder
public abstract class AbstractEventDto extends AbstractIdEntityDto {
	@Builder.Default
	private boolean hidden = false;

	@Builder.Default
	private boolean shareable = false;

	@NotBlank
	@Size(max = TEXT)
	private String name;

	@NotNull
	private LocalDate date;

	@NotNull
	private LocalTime startTime;

	@NotBlank
	@Size(max = TEXT)
	private String creator;

	@NotNull
	private EventTypeDto eventType;

	@Size(max = EMBEDDABLE_DESCRIPTION)
	private String description;

	@Size(max = TEXT)
	private String missionType;

	@Size(max = TEXT)
	private String missionLength;

	@Size(max = URL)
	private String pictureUrl;

	private Boolean reserveParticipating;

	private Set<EventDiscordInformationDto> discordInformation;

	private String ownerGuild;

	public String getPictureUrl() {
		return StringUtils.isNotEmpty(pictureUrl) ? pictureUrl : GuildUtils.getLogo(Long.parseLong(ownerGuild));
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

	public Optional<EventDiscordInformationDto> getDiscordInformation(String guildId) {
		return getDiscordInformation().stream()
				.filter(eventDiscordInformation -> eventDiscordInformation.getGuild().equals(guildId)).findAny();
	}
}
