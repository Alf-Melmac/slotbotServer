package de.webalf.slotbot.util.eventfield;

import de.webalf.slotbot.controller.website.FileWebController;
import de.webalf.slotbot.model.annotations.EventFieldDefault;
import de.webalf.slotbot.model.dtos.EventFieldDefaultDto;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.webalf.slotbot.model.enums.EventFieldType.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 27.04.2021
 */
@UtilityClass
@EventFieldDefault(eventTypeName = "Arma 3")
@Slf4j
public final class Arma3FieldUtils {
	private static final List<String> MOD_SETS = List.of("2201_ArmaMachtBock", "2201_ArmaMachtBock_GM", "2201_ArmaMachtBock_VN");

	private static final List<String> MAPS = List.of("A Shau Valley, Vietnam", "Aliabad Region", "Altis", "Anizay",
			"Ba Long, Quang Tri province, Vietnam", "Bukovina", "Bystrica", "Cao Bang, Vietnam", "Cam Lao Nam",
			"Chernarus (Herbst)", "Chernarus (Sommer)", "Chernarus (Winter)", "Da Krong, Quang Trie Vietnam",
			"Dak Pek, Kon Tum province, Vietnam", "Ðông Hà, Quang Tri, Vietnam", "Doung Island, Rung Sat Vietnam",
			"Die Wüste", "Fapovo v1.8", "Hebontes", "Hellanmaa", "Hellanmaa winter", "Ia Drâng, Gia Lai, Vietnam",
			"Khe Sanh", "Khe Sanh, Quang Tri, Vietnam (WIP)", "Khoramshahr", "Livonia", "Lowlands, Quang Ngai, Vietnam",
			"Malden 2035", "Napf Island A3", "NapfWinter Island A3", "Panthera (Winter) v3.9", "Panthera v3.91",
			"Phu Bai, Hue, Vietnam", "Phuoc Tuy Province, Vietnam", "Plei Trap, Kon Tum, Vietnam", "Porto",
			"Proving Grounds", "Rahmadi", "Rosche, Germany (2.0)", "Ruha", "Sahrani", "SEA, Lam Dong, Vietnam",
			"Shapur", "Southern Sahrani", "Stratis", "Song Bin Tanh, Mekong Delta, Vietnam",
			"Song Cu, Dong Nai Vietnam", "Summa", "Summa winter", "Takistan", "Takistan Mountains", "Tanoa",
			"United Sahrani", "Utes", "Vinjesvingen", "Virolahti", "Virtuelle Realität", "Weferlingen",
			"Weferlingen (Winter)", "Zargabad");

	@SuppressWarnings("unused") //EventFieldUtils#eventTypeNameToFieldDefaults
	static final List<EventFieldDefaultDto> FIELDS = List.of(
			EventFieldDefaultDto.builder().title("Respawn").type(BOOLEAN).build(),
			EventFieldDefaultDto.builder().title("Modset").type(TEXT_WITH_SELECTION).selection(MOD_SETS)
					.text(MOD_SETS.get(0)).build(),
			EventFieldDefaultDto.builder().title("Karte").type(SELECTION).selection(MAPS).build(),
			EventFieldDefaultDto.builder().title("Technischer Teleport").type(TEXT).build(),
			EventFieldDefaultDto.builder().title("Medic-System").type(TEXT).build(),
			EventFieldDefaultDto.builder().title("Missionszeit").type(TEXT).build(),
			EventFieldDefaultDto.builder().title("Navigation").type(TEXT).build()
	);

	public static final Pattern FILE_PATTERN = Pattern.compile("^(Arma_3_Preset_)?(.+)\\.html");
	private static final Map<String, String> DOWNLOADABLE_MOD_SETS = new HashMap<>();

	public static void fillDownloadableModSets(Set<String> fileNames) {
		DOWNLOADABLE_MOD_SETS.clear();
		fileNames.forEach(fileName -> {
			final Matcher matcher = FILE_PATTERN.matcher(fileName);
			matcher.find();
			DOWNLOADABLE_MOD_SETS.put(replaceSpecialNames(matcher.group(2)), fileName);
		});
		log.info("Found {} downloadable mod packs", DOWNLOADABLE_MOD_SETS.size());
	}

	private static String replaceSpecialNames(String matchedName) {
		switch (matchedName) {
			case "2012_ArmaMachtBock_Full":
				return "2012_ArmaMachtBock";
			case "2101_ArmaMachtBock_Full_v2":
				return "2101_ArmaMachtBock";
			case "2103_ArmaMachtBock_Full":
				return "2103_ArmaMachtBock";
			default:
				return matchedName;
		}
	}

	/**
	 * Matches the given string to a known modSet url
	 *
	 * @param modSet to get url for
	 * @return download url if known or null
	 */
	public static String getModSetUrl(String modSet) {
		if (modSet == null) {
			return null;
		}
		final String fileName = DOWNLOADABLE_MOD_SETS.get(modSet);
		if (fileName != null) {
			return linkTo(methodOn(FileWebController.class).getFile(fileName)).toUri().toString();
		}
		return null;
	}
}
