package de.webalf.slotbot.model.dtos;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * @author Alf
 * @since 23.06.2020
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EventDto extends AbstractIdEntityDto {
	@NotEmpty
	@Size(max = 80)
	private String name;

	@NotNull
	private LocalDate date;

	@NotNull
	private LocalTime startTime;

	//String is needed, because the discord IDs exceed the maximum size of a JavaScript number. See https://stackoverflow.com/questions/1379934/large-numbers-erroneously-rounded-in-javascript
	private String channel;

	private List<SquadDto> squadList;

	private String infoMsg;

	private String slotListMsg;

	@Size(max = 3200)
	private String description;
	@Size(max = 80)
	private String missionType;
	@Size(max = 80)
	private String missionLength;
	@Size(max = 80)
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

	@Builder
	public EventDto(long id,
	                String name,
	                LocalDate date,
	                LocalTime startTime,
	                String channel,
	                List<SquadDto> squadList,
	                String infoMsg,
	                String slotListMsg,
	                String description,
	                String missionType,
	                String missionLength,
	                Boolean reserveParticipating,
	                String modPack,
	                String map,
	                String missionTime,
	                String navigation,
	                String technicalTeleport,
	                String medicalSystem) {
		this.id = id;
		this.name = name;
		this.date = date;
		this.startTime = startTime;
		this.channel = channel;
		this.squadList = squadList;
		this.infoMsg = infoMsg;
		this.slotListMsg = slotListMsg;

		this.description = description;
		this.missionType = missionType;
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
