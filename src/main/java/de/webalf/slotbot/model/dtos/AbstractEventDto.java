package de.webalf.slotbot.model.dtos;

import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.util.DateUtils;
import de.webalf.slotbot.util.StringUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
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
	private Boolean hidden;

	private Boolean shareable;

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
		return StringUtils.isNotEmpty(pictureUrl) ? pictureUrl : GuildService.getLogo(Long.parseLong(ownerGuild));
	}

	public String getRawPictureUrl() {
		return pictureUrl;
	}

	public ZonedDateTime getDateTimeZoned() {
		return DateUtils.getDateTimeZoned(date, startTime);
	}

	public Optional<EventDiscordInformationDto> getDiscordInformation(String guildId) {
		return getDiscordInformation().stream()
				.filter(eventDiscordInformation -> eventDiscordInformation.getGuild().equals(guildId)).findAny();
	}
}
