package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import de.webalf.slotbot.util.EventUtils;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
		return EventUtils.getMissionTypeRespawnString(missionType, respawn);
	}

	@Getter
	private static final List<String> MAPS = new ArrayList<>();

	static {
		MAPS.add("Altis");
		MAPS.add("Bukovina");
		MAPS.add("Bystrica");
		MAPS.add("Chernarus (Herbst)");
		MAPS.add("Chernarus (Sommer)");
		MAPS.add("Chernarus (Winter)");
		MAPS.add("Die Wüste");
		MAPS.add("Kidal");
		MAPS.add("Kunduz, Afghanistan");
		MAPS.add("Livonia");
		MAPS.add("Malden 2035");
		MAPS.add("Porto");
		MAPS.add("Proving Grounds");
		MAPS.add("Rahmadi");
		MAPS.add("Sahrani");
		MAPS.add("Shapur");
		MAPS.add("Southern Sahrani");
		MAPS.add("Stratis");
		MAPS.add("Summa");
		MAPS.add("Summa winter");
		MAPS.add("Takistan");
		MAPS.add("Takistan Mountains");
		MAPS.add("Tanoa");
		MAPS.add("Tria");
		MAPS.add("United Sahrani");
		MAPS.add("Utes");
		MAPS.add("Virolahti");
		MAPS.add("Virtuelle Realität");
		MAPS.add("Zargabad");
	}

	public List<String> getMapsFiltered() {
		MAPS.remove(getMap());
		return MAPS;
	}
}
