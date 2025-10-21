package de.webalf.slotbot.feature.calendar;

import de.webalf.slotbot.feature.calendar.dto.ShortEventInformationDto;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 21.04.2022
 */
@UtilityClass
final class ShortEventInformationAssembler {
	ShortEventInformationDto toDto(@NonNull Event event) {
		int emptySlots = 0;
		int slotCount = 0;
		int emptyReserveSlots = 0;

		for (Squad squad : event.getSquadList()) {
			for (Slot slot : squad.getSlotList()) {
				if (!slot.isInReserve()) {
					if (slot.isEmpty()) {
						emptySlots++;
					}
					slotCount++;
				} else if (slot.isEmpty()) {
					emptyReserveSlots++;
				}
			}
		}

		return ShortEventInformationDto.builder()
				.emptySlotsCount(emptySlots)
				.slotCount(slotCount)
				.emptyReserveSlotsCount(emptyReserveSlots)
				.missionLength(event.getMissionLength())
				.build();
	}
}
