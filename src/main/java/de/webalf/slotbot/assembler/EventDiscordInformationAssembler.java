package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.EventDiscordInformation;
import de.webalf.slotbot.model.dtos.EventDiscordInformationDto;
import de.webalf.slotbot.util.LongUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
				.guild(Long.parseLong(dto.getGuild()))
				.infoMsg(LongUtils.parseLongWrapper(dto.getInfoMsg()))
				.slotListMsgPartOne(LongUtils.parseLongWrapper(dto.getSlotListMsgPartOne()))
				.slotListMsgPartTwo(LongUtils.parseLongWrapper(dto.getSlotListMsgPartTwo()))
				.build();
	}

	static Set<EventDiscordInformation> fromDtoIterable(Iterable<? extends EventDiscordInformationDto> dtos) {
		if (dtos == null) {
			return Collections.emptySet();
		}

		return StreamSupport.stream(dtos.spliterator(), true)
				.map(EventDiscordInformationAssembler::fromDto)
				.collect(Collectors.toUnmodifiableSet());
	}

	private static EventDiscordInformationDto toDto(EventDiscordInformation discordInformation) {
		if (discordInformation == null) {
			return null;
		}

		return EventDiscordInformationDto.builder()
				.channel(LongUtils.toString(discordInformation.getChannel()))
				.guild(LongUtils.toString(discordInformation.getGuild()))
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

	private String getChannelUrl(@NonNull EventDiscordInformation discordInformation) {
		return "discord://discordapp.com/channels/" + discordInformation.getGuild() + "/" + discordInformation.getChannel();
	}
}
