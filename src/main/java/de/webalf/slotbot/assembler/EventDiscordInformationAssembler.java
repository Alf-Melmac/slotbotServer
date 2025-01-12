package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.EventDiscordInformation;
import de.webalf.slotbot.model.dtos.EventDiscordInformationDto;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.util.LongUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Alf
 * @since 04.07.2021
 */
@Component
@RequiredArgsConstructor
public class EventDiscordInformationAssembler {
	private final GuildService guildService;

	public EventDiscordInformation fromDto(EventDiscordInformationDto dto) {
		if (dto == null) {
			return null;
		}

		return EventDiscordInformation.builder()
				.channel(Long.parseLong(dto.getChannel()))
				.guild(guildService.find(Long.parseLong(dto.getGuild())))
				.infoMsg(LongUtils.parseLongWrapper(dto.getInfoMsg()))
				.slotListMsgPartOne(LongUtils.parseLongWrapper(dto.getSlotListMsgPartOne()))
				.slotListMsgPartTwo(LongUtils.parseLongWrapper(dto.getSlotListMsgPartTwo()))
				.build();
	}
}
