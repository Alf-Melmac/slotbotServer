package de.webalf.slotbot.util;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.GuildDto;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * @author Alf
 * @since 07.02.2022
 */
@UtilityClass
public final class SlotUtils {
	/**
	 * Evaluates the reservation for a slot. Slot reservation or if empty the squad reservation.
	 * Doesn't return {@code reservedFor} if the whole squad is reserved for the {@link Guild} the squad is reserved for
	 *
	 * @param reservedFor reservation of slot
	 * @param squad       the slot is in
	 * @return effective reservation for display
	 * @see #getEffectiveReservedForDisplay(Guild, Guild, List)
	 */
	public static Guild getEffectiveReservedForDisplay(Guild reservedFor, @NonNull Squad squad) {
		return getEffectiveReservedForDisplay(reservedFor, squad.getReservedFor(), squad.getSlotList());
	}

	/**
	 * Evaluates the reservation for a slot. Slot reservation or if empty the squad reservation.
	 * Doesn't return {@code reservedFor} if the whole squad is reserved for the {@link GuildDto} the squad is reserved for
	 *
	 * @param reservedFor      reservation of slot
	 * @param squadReservedFor reservation of slots squad
	 * @param squadSlots       all slots of the squad containing this slot
	 * @return effective reservation for display
	 * @see #getEffectiveReservedForDisplay(Guild, Squad)
	 */
	public static Guild getEffectiveReservedForDisplay(Guild reservedFor, Guild squadReservedFor, List<Slot> squadSlots) {
		final Guild effectiveReservedFor = reservedFor != null ? reservedFor : squadReservedFor;
		if (squadReservedFor != null && squadReservedFor.equals(effectiveReservedFor) &&
				squadSlots.stream().allMatch(slot -> effectiveReservedFor.equals(slot.getEffectiveReservedFor()))) {
			return null;
		}
		return effectiveReservedFor;
	}
}
