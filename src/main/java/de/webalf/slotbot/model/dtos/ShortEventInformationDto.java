package de.webalf.slotbot.model.dtos;

import lombok.Builder;
import lombok.Value;

/**
 * @author Alf
 * @since 19.01.2021
 */
@Builder
@Value
public class ShortEventInformationDto {
	int emptySlotsCount;

	int slotCount;

	int emptyReserveSlotsCount;

	String missionLength;
}
