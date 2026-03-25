package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.EventDiscordInformation;
import de.webalf.slotbot.model.dtos.EventDiscordInformationDto;
import de.webalf.slotbot.service.GuildService;
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
				.channel(dto.getChannel())
				.guild(guildService.find(dto.getGuild()))
				.infoMsg(dto.getInfoMsg())
				.slotListMsgPartOne(dto.getSlotListMsgPartOne())
				.slotListMsgPartTwo(dto.getSlotListMsgPartTwo())
				.scheduledEvent(dto.getScheduledEvent())
				.build();
	}
}
