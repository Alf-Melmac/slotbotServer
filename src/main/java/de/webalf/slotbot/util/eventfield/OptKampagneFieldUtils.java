package de.webalf.slotbot.util.eventfield;

import de.webalf.slotbot.model.annotations.EventFieldDefault;
import de.webalf.slotbot.model.dtos.EventFieldDefaultDto;
import lombok.experimental.UtilityClass;

import java.util.List;

import static de.webalf.slotbot.model.enums.EventFieldType.BOOLEAN;
import static de.webalf.slotbot.model.enums.EventFieldType.TEXT;

/**
 * @author Alf
 * @since 14.05.2021
 */
@UtilityClass
@EventFieldDefault(eventTypeName = "OPT Kampagne")
public class OptKampagneFieldUtils {
	@SuppressWarnings("unused") //EventFieldUtils#eventTypeNameToFieldDefaults
	static final List<EventFieldDefaultDto> FIELDS = List.of(
			EventFieldDefaultDto.builder().title("Karte").type(TEXT).text("Virolahti").build(),
			EventFieldDefaultDto.builder().title("Modpack").type(TEXT)
					.text("http://repo.opt4.net/opt/.a3s/autoconfig").build(),
			EventFieldDefaultDto.builder().title("Teamspeak").type(TEXT)
					.text("ts3server://ts.opt4.net?port=9987").build(),
			EventFieldDefaultDto.builder().title("Respawn").type(BOOLEAN).text("true").build(),
			EventFieldDefaultDto.builder().title("Server").type(TEXT).text("arma.opt4.net:2302").build(),
			EventFieldDefaultDto.builder().title("Passwort").type(TEXT).text("opt2020").build()
	);
}
