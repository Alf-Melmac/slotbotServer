package de.webalf.slotbot.util;

import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 19.08.2022
 */
@UtilityClass
public final class GuildUtils {
	/**
	 * Get the id of the guild the squad is reserved for. As a string, because the id may be too big for JavaScript
	 *
	 * @param squad to get the reservation for
	 * @return id of the guild the squad is reserved for, or null if not reserved
	 */
	public static String getReservedFor(Squad squad) {
		return EntityUtils.getIdStringIfPresent(squad.getReservedFor());
	}

	/**
	 * Get the id of the guild the slot is reserved for. As a string, because the id may be too big for JavaScript
	 *
	 * @param slot to get the reservation for
	 * @return id of the guild the slot is reserved for, or null if not reserved
	 */
	public static String getReservedFor(Slot slot) {
		return EntityUtils.getIdStringIfPresent(slot.getReservedFor());
	}
}
