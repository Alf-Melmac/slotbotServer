package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.EventDiscordInformation;
import de.webalf.slotbot.model.dtos.EventDiscordInformationDto;
import de.webalf.slotbot.util.LongUtils;
import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 04.07.2021
 */
@UtilityClass
public final class EventDiscordInformationAssembler {
	public static EventDiscordInformation fromDto(EventDiscordInformationDto dto) {
		if (dto == null) {
			return null;
		}

		return EventDiscordInformation.builder()
				.channel(Long.parseLong(dto.getChannel()))
				.infoMsg(LongUtils.parseLongWrapper(dto.getInfoMsg()))
				.slotListMsgPartOne(LongUtils.parseLongWrapper(dto.getSlotListMsgPartOne()))
				.slotListMsgPartTwo(LongUtils.parseLongWrapper(dto.getSlotListMsgPartTwo()))
				.build();
	}

	public static EventDiscordInformationDto toDto(EventDiscordInformation discordInformation) {
		if (discordInformation == null) {
			return null;
		}

		return EventDiscordInformationDto.builder()
				.channel(LongUtils.toString(discordInformation.getChannel()))
				.infoMsg(LongUtils.toString(discordInformation.getInfoMsg()))
				.slotListMsgPartOne(LongUtils.toString(discordInformation.getSlotListMsgPartOne()))
				.slotListMsgPartTwo(LongUtils.toString(discordInformation.getSlotListMsgPartTwo()))
				.build();
	}
}
