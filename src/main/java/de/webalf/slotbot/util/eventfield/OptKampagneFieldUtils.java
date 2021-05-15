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
			EventFieldDefaultDto.builder().title("Karte").type(TEXT).text("Livonia").build(),
			EventFieldDefaultDto.builder().title("Modpack").type(TEXT)
					.text("https://opt4.net/forum/index.php?thread/219-wie-bekomme-ich-die-n%C3%B6tigen-mods-zum-mitspielen").build(),
			EventFieldDefaultDto.builder().title("Respawn").type(BOOLEAN).text("true").build()
	);
}
