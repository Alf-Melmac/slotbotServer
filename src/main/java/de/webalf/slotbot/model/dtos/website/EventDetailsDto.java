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
import java.util.List;
import java.util.stream.Collectors;

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
	private static final List<String> MISSION_TYPES = List.of("COOP", "Zeus", "TvT", "Nebenevent", "Anderes");
	private static final List<String> MOD_PACKS = List.of("2008_ArmaMachtBock", "Joined_Operations_2020");
	@Getter
	private static final List<String> MAPS = List.of("Altis", "Bukovina", "Bystrica", "Chernarus (Herbst)",
			"Chernarus (Sommer)", "Chernarus (Winter)", "Die Wüste", "Kidal", "Kunduz, Afghanistan", "Livonia",
			"Malden 2035", "Porto", "Proving Grounds", "Rahmadi", "Sahrani", "Shapur", "Southern Sahrani", "Stratis",
			"Summa", "Summa winter", "Takistan", "Takistan Mountains", "Tanoa", "Tria", "United Sahrani", "Utes",
			"Virolahti", "Virtuelle Realität", "Zargabad");

	/**
	 * Returns typical mod packs that can be filtered to allow pre-selection
	 *
	 * @param filter mod pack that shouldn't be shown
	 * @return known mod packs without the given one
	 */
	public static List<String> getModPacks(String filter) {
		return MOD_PACKS.stream().filter(modPack -> !modPack.equals(filter)).collect(Collectors.toList());
	}

	/**
	 * Returns known mission types, except the one saved in the event
	 *
	 * @return known mission types except the saved one
	 */
	public List<String> getMissionTypesFiltered() {
		return MISSION_TYPES.stream().filter(missionType -> !missionType.equals(getMissionType())).collect(Collectors.toList());
	}

	/**
	 * Returns known mod packs, except the one saved in the event
	 *
	 * @return known mod packs except the saved one
	 */
	public List<String> getModPacksFiltered() {
		return getModPacks(getModPack());
	}

	/**
	 * Returns known maps, except the one saved in the event
	 *
	 * @return known maps except the saved one
	 */
	public List<String> getMapsFiltered() {
		return MAPS.stream().filter(map -> !map.equals(getMap())).collect(Collectors.toList());
	}
}
