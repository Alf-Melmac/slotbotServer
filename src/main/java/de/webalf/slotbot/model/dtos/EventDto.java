package de.webalf.slotbot.model.dtos;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * @author Alf
 * @since 23.06.2020
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@SuperBuilder
public class EventDto extends AbstractIdEntityDto {
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

	private Boolean hidden;

	//String is needed, because the discord IDs exceed the maximum size of a JavaScript number. See https://stackoverflow.com/questions/1379934/large-numbers-erroneously-rounded-in-javascript
	private String channel;

	private List<SquadDto> squadList;

	private String infoMsg;

	private String slotListMsg;

	@Size(max = MessageEmbed.TEXT_MAX_LENGTH)
	private String description;

	@Size(max = 1666)
	private String pictureUrl;

	@Size(max = 80)
	private String missionType;
	private Boolean respawn;
	@Size(max = 80)
	private String missionLength;

	private Boolean reserveParticipating;
	@Size(max = 80)
	private String modPack;
	@Size(max = 80)
	private String map;
	@Size(max = 80)
	private String missionTime;
	@Size(max = 80)
	private String navigation;
	@Size(max = 80)
	private String technicalTeleport;
	@Size(max = 80)
	private String medicalSystem;
}
