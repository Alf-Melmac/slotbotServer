package de.webalf.slotbot.util;

import de.webalf.slotbot.controller.website.DownloadController;
import lombok.experimental.UtilityClass;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 10.11.2020
 */
@UtilityClass
public final class EventUtils {
	/**
	 * Combines the two given parameters to one string that can be shown
	 *
	 * @return compound of the two event params if present
	 */
	public static String getMissionTypeRespawnString(String missionType, Boolean respawn) {
		String compound = "";
		boolean respawnExists = respawn != null;
		if (StringUtils.isNotEmpty(missionType)) {
			compound += missionType;
			if (respawnExists) {
				compound += ", ";
			}
		}
		if (respawnExists) {
			compound += respawn ? "Respawn" : "Kein Respawn";
		}
		return compound;
	}

	public static String getModPackUrl(String modPack) {
		if (modPack == null) {
			return null;
		}
		switch (modPack) {
			case "2008_ArmaMachtBock":
				return linkTo(methodOn(DownloadController.class).getFile("Arma_3_Preset_2008_ArmaMachtBock.html")).toUri().toString();
			case "2012_ArmaMachtBock":
				return linkTo(methodOn(DownloadController.class).getFile("Arma_3_Preset_2012_ArmaMachtBock_Full.html")).toUri().toString();
			case "Joined_Operations_2020":
				return linkTo(methodOn(DownloadController.class).getFile("Joined_Operations_2020v2.html")).toUri().toString();
			case "Alliance_2021v1":
				return linkTo(methodOn(DownloadController.class).getFile("Alliance_2021v1.html")).toUri().toString();
			default:
				return null;
		}
	}
}
