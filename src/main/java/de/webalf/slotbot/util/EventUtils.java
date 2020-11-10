package de.webalf.slotbot.util;

import org.springframework.util.StringUtils;

/**
 * @author Alf
 * @since 10.11.2020
 */
public class EventUtils {
	/**
	 * Combines the two given parameters to one string that can be shown
	 *
	 * @return compound of the two event params if present
	 */
	public static String getMissionTypeRespawnString(String missionType, Boolean respawn) {
		String compound = "";
		boolean respawnExists = respawn != null;
		if (!StringUtils.isEmpty(missionType)) {
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
}
