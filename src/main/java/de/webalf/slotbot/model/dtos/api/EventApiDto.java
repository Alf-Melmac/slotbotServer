package de.webalf.slotbot.model.dtos.api;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import de.webalf.slotbot.model.dtos.SquadDto;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * @author Alf
 * @since 04.11.2020
 */
@EqualsAndHashCode(callSuper = true)
@Value
public class EventApiDto extends AbstractIdEntityDto {
	@NotBlank
	String url;

	@NotBlank
	@Size(max = 80)
	String name;

	@NotNull
	LocalDate date;

	@NotNull
	LocalTime startTime;

	//String is needed, because the discord IDs exceed the maximum size of a JavaScript number. See https://stackoverflow.com/questions/1379934/large-numbers-erroneously-rounded-in-javascript
	String channel;

	List<SquadDto> squadList;

	String infoMsg;

	String slotListMsg;

	@Size(max = 3200)
	String description;
	@Size(max = 1666)
	String pictureUrl;
	@Size(max = 80)
	String missionType;
	Boolean respawn;
	@Size(max = 80)
	String missionLength;
	Boolean reserveParticipating;
	@Size(max = 80)
	String modPack;
	@Size(max = 80)
	String map;
	@Size(max = 80)
	String missionTime;
	@Size(max = 80)
	String navigation;
	@Size(max = 80)
	String technicalTeleport;
	@Size(max = 80)
	String medicalSystem;

	@Builder
	public EventApiDto(String url,
	                   long id,
	                   String name,
	                   LocalDate date,
	                   LocalTime startTime,
	                   String channel,
	                   List<SquadDto> squadList,
	                   String infoMsg,
	                   String slotListMsg,
	                   String description,
	                   String pictureUrl,
	                   String missionType,
	                   Boolean respawn,
	                   String missionLength,
	                   Boolean reserveParticipating,
	                   String modPack,
	                   String map,
	                   String missionTime,
	                   String navigation,
	                   String technicalTeleport,
	                   String medicalSystem) {
		this.url = url;

		this.id = id;
		this.name = name;
		this.date = date;
		this.startTime = startTime;
		this.channel = channel;
		this.squadList = squadList;
		this.infoMsg = infoMsg;
		this.slotListMsg = slotListMsg;

		this.description = description;
		this.pictureUrl = pictureUrl;
		this.missionType = missionType;
		this.respawn = respawn;
		this.missionLength = missionLength;
		this.reserveParticipating = reserveParticipating;
		this.modPack = modPack;
		this.map = map;
		this.missionTime = missionTime;
		this.navigation = navigation;
		this.technicalTeleport = technicalTeleport;
		this.medicalSystem = medicalSystem;
	}
}
