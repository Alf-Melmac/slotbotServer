package de.webalf.slotbot.util.eventfield;

import de.webalf.slotbot.model.annotations.EventFieldDefault;
import de.webalf.slotbot.model.dtos.EventFieldDefaultDto;
import lombok.experimental.UtilityClass;

import java.util.List;

import static de.webalf.slotbot.model.enums.EventFieldType.TEXT;
import static de.webalf.slotbot.model.enums.EventFieldType.TEXT_WITH_SELECTION;

/**
 * @author Alf
 * @since 26.10.2021
 */
@UtilityClass
@EventFieldDefault(eventTypeName = "Deutsche Arma Allianz")
public class DAAFieldUtils {
	private static final List<String> MAPS = List.of("Altis", "Bukovina", "Bystrica", "Chernarus (Herbst)",
			"Chernarus (Sommer)", "Chernarus (Winter)", "Die Wüste", "Livonia", "Malden 2035", "Porto", "Proving Grounds",
			"Rahmadi", "Sahrani", "Shapur", "Southern Sahrani", "Stratis", "Takistan", "Takistan Mountains", "Tanoa",
			"United Sahrani", "Utes", "Virtuelle Realität", "Zargabad");

	@SuppressWarnings("unused") //EventFieldUtils#eventTypeNameToFieldDefaults
	static final List<EventFieldDefaultDto> FIELDS = List.of(
			EventFieldDefaultDto.builder().title("Karte").type(TEXT_WITH_SELECTION).selection(MAPS).build(),
			EventFieldDefaultDto.builder().title("Zeitlicher Ablauf").type(TEXT).text("Führungsbesprechung 19:00 Uhr, \nSlotting bis 19:45 Uhr, \nMissionsbeginn 20 Uhr").build(),
			EventFieldDefaultDto.builder().title("Modset").type(TEXT).build(),
			EventFieldDefaultDto.builder().title("Teamspeak").type(TEXT).build()
	);
}
