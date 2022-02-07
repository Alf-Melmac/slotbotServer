package de.webalf.slotbot.util;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.GuildDto;
import de.webalf.slotbot.model.dtos.referenceless.SlotReferencelessDto;
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
	 * @see #getEffectiveReservedForDisplay(GuildDto, GuildDto, List)
	 */
	public Guild getEffectiveReservedForDisplay(Guild reservedFor, @NonNull Squad squad) {
		final Guild effectiveReservedFor = reservedFor != null ? reservedFor : squad.getReservedFor();
		if (squad.getReservedFor() != null && squad.getReservedFor().equals(effectiveReservedFor) &&
				squad.getSlotList().stream().allMatch(slot -> effectiveReservedFor.equals(slot.getEffectiveReservedFor()))) {
			return null;
		}
		return effectiveReservedFor;
	}

	/**
	 * Evaluates the reservation for a slot. Slot reservation or if empty the squad reservation.
	 * Doesn't return {@code squadReservedFor} if the whole squad is reserved for the {@link GuildDto} the squad is reserved for
	 *
	 * @param reservedFor      reservation of slot
	 * @param squadReservedFor reservation of slots squad
	 * @param slotList         of slots squad
	 * @return effective reservation for display
	 * @see #getEffectiveReservedForDisplay(Guild, Squad)
	 */
	public GuildDto getEffectiveReservedForDisplay(GuildDto reservedFor, GuildDto squadReservedFor, List<? extends SlotReferencelessDto> slotList) {
		final GuildDto effectiveReservedFor = reservedFor != null ? reservedFor : squadReservedFor;
		if (squadReservedFor != null && squadReservedFor.equals(effectiveReservedFor) &&
				slotList.stream().allMatch(slot -> effectiveReservedFor.equals(slot.getReservedFor() != null ? slot.getReservedFor() : squadReservedFor))) {
			return null;
		}
		return effectiveReservedFor;
	}
}
