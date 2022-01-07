package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.EventDiscordInformation;
import de.webalf.slotbot.model.dtos.EventDiscordInformationDto;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.util.LongUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 04.07.2021
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
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

	Set<EventDiscordInformation> fromDtoIterable(Iterable<? extends EventDiscordInformationDto> dtos) {
		if (dtos == null) {
			return Collections.emptySet();
		}

		return StreamSupport.stream(dtos.spliterator(), true)
				.map(this::fromDto)
				.collect(Collectors.toUnmodifiableSet());
	}

	private static EventDiscordInformationDto toDto(EventDiscordInformation discordInformation) {
		if (discordInformation == null) {
			return null;
		}

		return EventDiscordInformationDto.builder()
				.channel(LongUtils.toString(discordInformation.getChannel()))
				.guild(LongUtils.toString(discordInformation.getGuild().getId()))
				.infoMsg(LongUtils.toString(discordInformation.getInfoMsg()))
				.slotListMsgPartOne(LongUtils.toString(discordInformation.getSlotListMsgPartOne()))
				.slotListMsgPartTwo(LongUtils.toString(discordInformation.getSlotListMsgPartTwo()))
				.channelUrl(getChannelUrl(discordInformation))
				.build();
	}

	static Set<EventDiscordInformationDto> toDtoSet(Iterable<? extends EventDiscordInformation> eventFields) {
		return StreamSupport.stream(eventFields.spliterator(), true)
				.map(EventDiscordInformationAssembler::toDto)
				.collect(Collectors.toUnmodifiableSet());
	}

	private static String getChannelUrl(@NonNull EventDiscordInformation discordInformation) {
		return "discord://discordapp.com/channels/" + discordInformation.getGuild() + "/" + discordInformation.getChannel();
	}
}
