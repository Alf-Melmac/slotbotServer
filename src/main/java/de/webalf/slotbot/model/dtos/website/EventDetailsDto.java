package de.webalf.slotbot.model.dtos.website;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import de.webalf.slotbot.util.EventUtils;
import de.webalf.slotbot.util.ListUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.SuperBuilder;

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
@SuperBuilder
public class EventDetailsDto extends AbstractIdEntityDto {
	@NotBlank
	@Size(max = 80)
	String name;

	@NotNull
	LocalDate date;

	@NotNull
	LocalTime startTime;

	@NotBlank
	@Size(max = 80)
	String creator;

	Boolean hidden;

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
	String modPackUrl;
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

	public String getMissionTypeRespawnString() {
		return EventUtils.getMissionTypeRespawnString(missionType, respawn);
	}

	@Getter
	private static final List<String> MISSION_TYPES = List.of("COOP", "COOP+", "Zeus", "TvT", "Training", "Spezial", "Anderes");
	@Getter
	private static final List<String> MOD_PACKS = List.of("2012_ArmaMachtBock", "Joined_Operations_2020", "Alliance_2021v1");
	@Getter
	private static final List<String> MAPS = List.of("Aliabad Region", "Altis", "Anizay", "Beketow", "Bukovina",
			"Bystrica", "Chernarus (Herbst)", "Chernarus (Sommer)", "Chernarus (Winter)", "Chernarus 2020",
			"Chongo, Angola v1.30", "CLA Clafghan", "Desert Island", "Die Wüste", "Everon", "Hebontes", "Hellanmaa",
			"Hellanmaa winter", "Khoramshahr", "Kolgujev", "Livonia", "Malden", "Malden 2035", "Nogova", "Porto",
			"Prei Khmaoch Luong", "Proving Grounds", "Rahmadi", "Rosche, Germany (2.0)", "Ruha", "Sahrani",
			"SE Angola 1974 v1.30", "Shapur", "Southern Sahrani", "Stratis", "Summa", "Summa winter", "Takistan",
			"Takistan Mountains", "Tanoa", "United Sahrani", "Utes", "Vinjesvingen", "Virolahti", "Virtuelle Realität",
			"Zargabad",

			"Kidal", "Kunduz, Afghanistan", "Tria"); //OLD

	/**
	 * Returns known mission types, except the one saved in the event
	 *
	 * @return known mission types except the saved one
	 */
	public List<String> getMissionTypesFiltered() {
		return ListUtils.getListFiltered(MISSION_TYPES, getMissionType());
	}

	/**
	 * Returns known mod packs, except the one saved in the event
	 *
	 * @return known mod packs except the saved one
	 */
	public List<String> getModPacksFiltered() {
		return ListUtils.getListFiltered(MOD_PACKS, getModPack());
	}

	/**
	 * Returns known maps, except the one saved in the event
	 *
	 * @return known maps except the saved one
	 */
	public List<String> getMapsFiltered() {
		return ListUtils.getListFiltered(MAPS, getMap());
	}
}
