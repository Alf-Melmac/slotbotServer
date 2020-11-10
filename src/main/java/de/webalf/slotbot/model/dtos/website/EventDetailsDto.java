package de.webalf.slotbot.model.dtos.website;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
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
 * @since 30.10.2020
 */
@EqualsAndHashCode(callSuper = true)
@Value
public class EventDetailsDto extends AbstractIdEntityDto {
	@NotBlank
	@Size(max = 80)
	String name;

	@NotNull
	LocalDate date;

	@NotNull
	LocalTime startTime;

	String channelUrl;

	List<EventDetailsSquadDto> squadList;

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
	public EventDetailsDto(long id,
	                       String name,
	                       LocalDate date,
	                       LocalTime startTime,
	                       String channelUrl,
	                       List<EventDetailsSquadDto> squadList,
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
		this.id = id;
		this.name = name;
		this.date = date;
		this.startTime = startTime;
		this.channelUrl = channelUrl;
		this.squadList = squadList;

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

	public String getMissionTypeRespawnString() {
		String compound = "";
		boolean respawnExists = respawn != null;
		if (StringUtils.isNotBlank(missionType)) {
			compound += missionType;
			if (respawnExists) {
				compound += ", ";
			}
		}
		if (respawnExists) {
			compound += respawn ? "Respawn" : "Kein Respawn";
		}
		return compound;
	}
}
