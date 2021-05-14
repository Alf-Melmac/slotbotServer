package de.webalf.slotbot.util.eventfield;

import de.webalf.slotbot.controller.website.FileController;
import de.webalf.slotbot.model.dtos.EventFieldDefaultDto;
import lombok.experimental.UtilityClass;

import java.util.List;

import static de.webalf.slotbot.model.enums.EventFieldType.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 27.04.2021
 */
@UtilityClass
public class Arma3FieldUtils {
	public static final String EVENT_TYPE_NAME = "Arma 3";

	private static final List<String> MOD_PACKS = List.of("2103_ArmaMachtBock", "2104_ArmaMachtBock_GM", "2105_ArmaMachtBock_VN",
			"Joined_Operations_2020", "Alliance_2021v1");

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

	public static final List<EventFieldDefaultDto> FIELDS = List.of(
			EventFieldDefaultDto.builder().title("Respawn").type(BOOLEAN).build(),
			EventFieldDefaultDto.builder().title("Modpack").type(TEXT_WITH_SELECTION).selection(MOD_PACKS)
					.text(MOD_PACKS.get(0)).build(),
			EventFieldDefaultDto.builder().title("Karte").type(SELECTION).selection(MAPS).build(),
			EventFieldDefaultDto.builder().title("Technischer Teleport").type(TEXT).build(),
			EventFieldDefaultDto.builder().title("Medic-System").type(TEXT).build(),
			EventFieldDefaultDto.builder().title("Missionszeit").type(TEXT).build(),
			EventFieldDefaultDto.builder().title("Navigation").type(TEXT).build()
	);

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
		switch (modPack) {
			case "2008_ArmaMachtBock":
				return linkTo(methodOn(FileController.class).getFile("Arma_3_Preset_2008_ArmaMachtBock.html")).toUri().toString();
			case "2012_ArmaMachtBock":
				return linkTo(methodOn(FileController.class).getFile("Arma_3_Preset_2012_ArmaMachtBock_Full.html")).toUri().toString();
			case "2101_ArmaMachtBock":
				return linkTo(methodOn(FileController.class).getFile("Arma_3_Preset_2101_ArmaMachtBock_Full_v2.html")).toUri().toString();
			case "2103_ArmaMachtBock":
				return linkTo(methodOn(FileController.class).getFile("Arma_3_Preset_2103_ArmaMachtBock_Full.html")).toUri().toString();
			case "2102_Event":
				return linkTo(methodOn(FileController.class).getFile("Arma_3_Preset_2102_Event.html")).toUri().toString();
			case "2104_ArmaMachtBock_GM":
				return linkTo(methodOn(FileController.class).getFile("Arma_3_Preset_2104_ArmaMachtBock_GM.html")).toUri().toString();
			case "2105_ArmaMachtBock_VN":
				return linkTo(methodOn(FileController.class).getFile("2105_ArmaMachtBock_VN.html")).toUri().toString();
			case "Joined_Operations_2020":
				return linkTo(methodOn(FileController.class).getFile("Joined_Operations_2020v2.html")).toUri().toString();
			case "Alliance_2021v1":
				return linkTo(methodOn(FileController.class).getFile("Alliance_2021v1.html")).toUri().toString();
			default:
				return null;
		}
	}
}
