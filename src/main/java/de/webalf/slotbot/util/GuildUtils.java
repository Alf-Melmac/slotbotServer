package de.webalf.slotbot.util;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 19.08.2022
 */
@UtilityClass
public final class GuildUtils {
	public static String getReservedFor(Squad squad) {
		return getReservedFor(squad.getReservedFor());
	}

	public static String getReservedFor(Slot slot) {
		return getReservedFor(slot.getReservedFor());
	}

	private static String getReservedFor(Guild reservedFor) {
		if (reservedFor == null) {
			return null;
		}
		return Long.toString(reservedFor.getId());
	}
}
