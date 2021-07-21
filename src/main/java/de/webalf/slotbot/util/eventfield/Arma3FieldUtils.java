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
	private static final List<String> MOD_PACKS = List.of("2103_ArmaMachtBock", "2104_ArmaMachtBock_GM", "2105_ArmaMachtBock_VN",
			"Alliance_2021v3");

	private static final List<String> MAPS = List.of("A Shau Valley, Vietnam", "Aliabad Region", "Altis", "Anizay",
			"Ba Long, Quang Tri province, Vietnam", "Beketow", "Bukovina", "Bystrica", "Cao Bang, Vietnam", "Cam Lao Nam",
			"Chernarus (Herbst)", "Chernarus (Sommer)", "Chernarus (Winter)", "Chernarus 2020", "Chongo, Angola v1.30",
			"CLA Clafghan", "Da Krong, Quang Trie Vietnam", "Dak Pek, Kon Tum province, Vietnam", "Desert Island", "Ðông Hà",
			"Doung Island, Rung Sat Vietnam", "Quang Tri, Vietnam", "Die Wüste", "Everon", "Hebontes", "Hellanmaa",
			"Hellanmaa winter", "Ia Drâng, Gia Lai, Vietnam", "Khe Sanh, Quang Tri, Vietnam (WIP)", "Khoramshahr",
			"Kolgujev", "Livonia", "Lowlands, Quang Ngai, Vietnam", "Malden", "Malden 2035", "Nogova", "Phu Bai, Hue, Vietnam",
			"Phuoc Tuy Province, Vietnam", "Plei Trap, Kon Tum, Vietnam", "Porto", "Prei Khmaoch Luong", "Proving Grounds",
			"Rahmadi", "Rosche, Germany (2.0)", "Ruha", "Sahrani", "SE Angola 1974 v1.30", "SEA, Lam Dong, Vietnam",
			"Shapur", "Song Bin Tanh, Mekong Delta, Vietnam", "Song Cu, Dong Nai Vietnam", "Southern Sahrani", "Stratis",
			"Summa", "Summa winter", "Takistan", "Takistan Mountains", "Tanoa", "United Sahrani", "Utes", "Vinjesvingen",
			"Virolahti", "Virtuelle Realität", "Werferlingen", "Zargabad");

	@SuppressWarnings("unused") //EventFieldUtils#eventTypeNameToFieldDefaults
	static final List<EventFieldDefaultDto> FIELDS = List.of(
			EventFieldDefaultDto.builder().title("Respawn").type(BOOLEAN).build(),
			EventFieldDefaultDto.builder().title("Modpack").type(TEXT_WITH_SELECTION).selection(MOD_PACKS)
					.text(MOD_PACKS.get(0)).build(),
			EventFieldDefaultDto.builder().title("Karte").type(SELECTION).selection(MAPS).build(),
			EventFieldDefaultDto.builder().title("Technischer Teleport").type(TEXT).build(),
			EventFieldDefaultDto.builder().title("Medic-System").type(TEXT).build(),
			EventFieldDefaultDto.builder().title("Missionszeit").type(TEXT).build(),
			EventFieldDefaultDto.builder().title("Navigation").type(TEXT).build()
	);

	public static final Pattern FILE_PATTERN = Pattern.compile("^(Arma_3_Preset_)?(.+)\\.html");
	private static final Map<String, String> DOWNLOADABLE_MOD_PACKS = new HashMap<>();

	public static void fillDownloadableModPacks(Set<String> fileNames) {
		DOWNLOADABLE_MOD_PACKS.clear();
		fileNames.forEach(fileName -> {
			final Matcher matcher = FILE_PATTERN.matcher(fileName);
			matcher.find();
			DOWNLOADABLE_MOD_PACKS.put(replaceSpecialNames(matcher.group(2)), fileName);
		});
		log.info("Found {} downloadable mod packs", DOWNLOADABLE_MOD_PACKS.size());
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
	 * Matches the given string to a known modpack url
	 *
	 * @param modPack to get url for
	 * @return download url if known or null
	 */
	public static String getModPackUrl(String modPack) {
		if (modPack == null) {
			return null;
		}
		final String fileName = DOWNLOADABLE_MOD_PACKS.get(modPack);
		if (fileName != null) {
			return linkTo(methodOn(FileWebController.class).getFile(fileName)).toUri().toString();
		}
		return null;
	}
}
