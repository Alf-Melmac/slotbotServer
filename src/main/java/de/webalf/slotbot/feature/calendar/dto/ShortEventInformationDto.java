package de.webalf.slotbot.feature.calendar.dto;

import lombok.Builder;

/**
 * @author Alf
 * @since 19.01.2021
 */
@Builder
public record ShortEventInformationDto(
		int emptySlotsCount,
		int slotCount,
		int emptyReserveSlotsCount,
		String missionLength
) {}
