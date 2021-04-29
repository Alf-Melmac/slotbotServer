package de.webalf.slotbot.util.eventfield;

import de.webalf.slotbot.model.dtos.EventFieldDefaultDto;
import lombok.experimental.UtilityClass;

import java.util.List;

import static de.webalf.slotbot.model.enums.EventFieldType.*;

/**
 * @author Alf
 * @since 27.04.2021
 */
@UtilityClass
public class Arma3FieldUtils {
	public static final String EVENT_TYPE_NAME = "Arma 3";

	private static final List<String> MOD_PACKS = List.of("2103_ArmaMachtBock", "Joined_Operations_2020", "Alliance_2021v1");

	private static final List<String> MAPS = List.of("Aliabad Region", "Altis", "Anizay", "Beketow", "Bukovina",
			"Bystrica", "Chernarus (Herbst)", "Chernarus (Sommer)", "Chernarus (Winter)", "Chernarus 2020",
			"Chongo, Angola v1.30", "CLA Clafghan", "Desert Island", "Die Wüste", "Everon", "Hebontes", "Hellanmaa",
			"Hellanmaa winter", "Khoramshahr", "Kolgujev", "Livonia", "Malden", "Malden 2035", "Nogova", "Porto",
			"Prei Khmaoch Luong", "Proving Grounds", "Rahmadi", "Rosche, Germany (2.0)", "Ruha", "Sahrani",
			"SE Angola 1974 v1.30", "Shapur", "Southern Sahrani", "Stratis", "Summa", "Summa winter", "Takistan",
			"Takistan Mountains", "Tanoa", "United Sahrani", "Utes", "Vinjesvingen", "Virolahti", "Virtuelle Realität",
			"Zargabad");

	public static final List<EventFieldDefaultDto> FIELDS = List.of(
			EventFieldDefaultDto.builder().title("Respawn").type(BOOLEAN).build(),
			EventFieldDefaultDto.builder().title("Modpack").type(TEXT_WITH_SELECTION).selection(MOD_PACKS).build(),
			EventFieldDefaultDto.builder().title("Karte").type(SELECTION).selection(MAPS).build(),
			EventFieldDefaultDto.builder().title("Technischer Teleport").type(TEXT).build(),
			EventFieldDefaultDto.builder().title("Medic-System").type(TEXT).build(),
			EventFieldDefaultDto.builder().title("Missionszeit").type(TEXT).build(),
			EventFieldDefaultDto.builder().title("Navigation").type(TEXT).build()
	);
}