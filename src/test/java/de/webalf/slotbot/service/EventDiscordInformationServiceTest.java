package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.EventDiscordInformationAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.EventDiscordInformation;
import de.webalf.slotbot.model.EventDiscordInformation_;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.dtos.EventDiscordInformationDto;
import de.webalf.slotbot.repository.EventDiscordInformationRepository;
import de.webalf.slotbot.util.LongUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static de.webalf.slotbot.AssertionUtils.assertMessageEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * @author Alf
 * @since 04.10.2021
 */
@ExtendWith(MockitoExtension.class)
class EventDiscordInformationServiceTest {
	@Mock
	private EventDiscordInformationRepository discordInformationRepository;
	@Mock
	private EventDiscordInformationAssembler discordInformationAssembler;

	@InjectMocks
	private EventDiscordInformationService sut;

	//existsByChannelInDtos
	@Test
	void existsByChannelCanNotFindChannel() {
		final long channelId = 123;

		when(discordInformationRepository.existsByChannelIn(Set.of(channelId))).thenReturn(false);

		assertFalse(sut.existsByChannelInDtos(
				Set.of(EventDiscordInformationDto.builder().channel(Long.toString(channelId)).build())
		));
	}

	@Test
	void existsByChannelFindsChannel() {
		final long channelId = 123;

		when(discordInformationRepository.existsByChannelIn(Set.of(channelId))).thenReturn(true);

		assertTrue(sut.existsByChannelInDtos(
				Set.of(EventDiscordInformationDto.builder().channel(Long.toString(channelId)).build())
		));
	}

	//updateDiscordInformation
	@Test
	void updateDiscordInformationAddsInformation() {
		final long guildId = 123;
		final long channelId = 1123;

		final Event event = Event.builder().discordInformation(new HashSet<>()).build();

		final Set<EventDiscordInformationDto> informationDtos = buildInformationSet(Map.of(guildId, channelId));
		final Set<EventDiscordInformation> expected = buildActualInformationSet(informationDtos, event);
		mockAssembler(channelId, guildId);

		sut.updateDiscordInformation(informationDtos, event);

		assertThat(event.getDiscordInformation())
				.usingRecursiveComparison()
				.ignoringCollectionOrder()
				.ignoringFields(EventDiscordInformation_.INFO_MSG, EventDiscordInformation_.SLOT_LIST_MSG_PART_ONE, EventDiscordInformation_.SLOT_LIST_MSG_PART_TWO)
				.isEqualTo(expected);
	}

	@Test
	void updateDiscordInformationAddsInformationToAlreadyExisting() {
		final long existingGuildId = 123;
		final long existingGuildChannel = 1123;
		final long otherGuildId = 456;
		final long otherGuildChannel = 4456;

		final Event event = buildEvent(existingGuildId, existingGuildChannel);

		final Set<EventDiscordInformationDto> informationDtos = buildInformationSet(Map.of(existingGuildId, existingGuildChannel, otherGuildId, otherGuildChannel));
		final Set<EventDiscordInformation> expected = buildActualInformationSet(informationDtos, event);
		mockAssembler(otherGuildChannel, otherGuildId);

		sut.updateDiscordInformation(informationDtos, event);

		assertThat(event.getDiscordInformation())
				.usingRecursiveComparison()
				.ignoringCollectionOrder()
				.ignoringFields(EventDiscordInformation_.INFO_MSG, EventDiscordInformation_.SLOT_LIST_MSG_PART_ONE, EventDiscordInformation_.SLOT_LIST_MSG_PART_TWO)
				.isEqualTo(expected);
	}

	@Test
	void updateDiscordInformationThrowsIfGuildAlreadyHasEvent() {
		final long existingGuildId = 123;
		final long existingGuildChannel = 1123;
		final long otherGuildChannel = 4456;

		final Event event = buildEvent(existingGuildId, existingGuildChannel);

		final Set<EventDiscordInformationDto> informationDtos = buildInformationSet(Map.of(existingGuildId, otherGuildChannel));
		final BusinessRuntimeException exception = assertThrows(BusinessRuntimeException.class, () ->
				sut.updateDiscordInformation(informationDtos, event));
		assertMessageEquals("Mindestens einer der übergebenen Guilds ist dieses Event bereits zugeordnet.", exception);
	}

	@Test
	void updateDiscordInformationThrowsIfChannelAlreadyHasEvent() {
		final long existingEventChannel = 1123;
		when(discordInformationRepository.existsByChannelIn(Set.of(existingEventChannel))).thenReturn(true);

		final Event event = Event.builder().discordInformation(Collections.emptySet()).build();

		final long guildId = 123;
		final Set<EventDiscordInformationDto> informationDtos = buildInformationSet(Map.of(guildId, existingEventChannel));
		final BusinessRuntimeException exception = assertThrows(BusinessRuntimeException.class, () ->
				sut.updateDiscordInformation(informationDtos, event));
		assertMessageEquals("In mindestens einem der angegebenen Kanäle gibt es bereits ein Event.", exception);
	}

	private Event buildEvent(long guildId, long channel) {
		final Set<EventDiscordInformation> information = new HashSet<>();
		information.add(EventDiscordInformation.builder().guild(Guild.builder().id(guildId).build()).channel(channel).build());
		final Event event = Event.builder().discordInformation(information).build();
		event.getDiscordInformation().forEach(eventDiscordInformation -> eventDiscordInformation.setEvent(event));
		return event;
	}

	private Set<EventDiscordInformationDto> buildInformationSet(Map<Long, Long> guilds) {
		return guilds.entrySet().stream()
				.map(guild -> EventDiscordInformationDto.builder()
						.guild(Long.toString(guild.getKey()))
						.channel(Long.toString(guild.getValue()))
						.build())
				.collect(Collectors.toSet());
	}

	private Set<EventDiscordInformation> buildActualInformationSet(Set<EventDiscordInformationDto> informationDtos, Event event) {
		return informationDtos.stream().map(informationDto -> EventDiscordInformation.builder()
						.event(event)
						.guild(Guild.builder().id(LongUtils.parseLong(informationDto.getGuild())).build())
						.channel(LongUtils.parseLong(informationDto.getChannel()))
						.infoMsg(LongUtils.parseLong(informationDto.getInfoMsg()))
						.slotListMsgPartOne(LongUtils.parseLong(informationDto.getSlotListMsgPartOne()))
						.slotListMsgPartTwo(LongUtils.parseLong(informationDto.getSlotListMsgPartTwo()))
						.build())
				.collect(Collectors.toUnmodifiableSet());
	}

	private void mockAssembler(long channel, long guild) {
		when(discordInformationAssembler.fromDto(EventDiscordInformationDto.builder().channel(Long.toString(channel)).guild(Long.toString(guild)).build()))
				.thenReturn(EventDiscordInformation.builder().channel(channel).guild(Guild.builder().id(guild).build()).build());
	}
}