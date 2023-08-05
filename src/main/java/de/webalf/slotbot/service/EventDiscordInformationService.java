package de.webalf.slotbot.service;

import de.webalf.slotbot.assembler.EventDiscordInformationAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.EventDiscordInformation;
import de.webalf.slotbot.model.dtos.EventDiscordInformationDto;
import de.webalf.slotbot.repository.EventDiscordInformationRepository;
import de.webalf.slotbot.model.event.EventArchiveEvent;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Alf
 * @since 04.07.2021
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EventDiscordInformationService {
	private final EventDiscordInformationRepository discordInformationRepository;
	private final EventDiscordInformationAssembler discordInformationAssembler;
	private final ApplicationEventPublisher eventPublisher;

	/**
	 * Returns an optional for the event associated with the given channelId
	 *
	 * @param channel to find event for
	 * @return Event found by channel or empty optional
	 */
	Optional<Event> findEventByChannel(long channel) {
		return discordInformationRepository.findById(channel)
				.map(EventDiscordInformation::getEvent);
	}

	/**
	 * Checks if an information with any of the given channelIds already exists
	 *
	 * @param channelIds to check existence of
	 * @return true if there is a saved information with any of the given ids
	 */
	private boolean existsByChannelIn(Set<Long> channelIds) {
		return discordInformationRepository.existsByChannelIn(channelIds);
	}

	boolean existsByChannelInDtos(@NonNull Set<EventDiscordInformationDto> discordInformationDtos) {
		return existsByChannelIn(discordInformationDtos.stream()
				.map(discordInformationDto -> Long.parseLong(discordInformationDto.getChannel())).collect(Collectors.toUnmodifiableSet()));
	}

	/**
	 * Updates the discord information for the given event.
	 * Existing information is not changed, only new information is added. To remove an information, see {@link Event#archive(long)}
	 *
	 * @param discordInformationDtos new discord information, can include already existing information
	 * @param event                  to update discord information of
	 * @throws BusinessRuntimeException If in any of the passed guilds the event has already been assigned to another channel or any passed channel has already been assigned another event.
	 */
	void updateDiscordInformation(Set<EventDiscordInformationDto> discordInformationDtos, @NonNull Event event) {
		if (isEmpty(discordInformationDtos)) {
			return;
		}

		final Set<EventDiscordInformation> eventInformation = event.getDiscordInformation();
		//Remove already present information
		final Set<EventDiscordInformationDto> filteredInformationDtos = discordInformationDtos.stream().filter(informationDto -> eventInformation.stream()
						.noneMatch(information -> information.getGuild().getId() == Long.parseLong(informationDto.getGuild()) &&
								information.getChannel() == Long.parseLong(informationDto.getChannel())))
				.collect(Collectors.toSet());

		if (eventInformation.stream().anyMatch(information -> filteredInformationDtos.stream()
				.anyMatch(discordInformationDto -> information.getGuild().getId() == Long.parseLong(discordInformationDto.getGuild())))) {
			throw BusinessRuntimeException.builder().title("Mindestens einer der übergebenen Guilds ist dieses Event bereits zugeordnet.").build();
		} else if (existsByChannelInDtos(filteredInformationDtos)) {
			throw BusinessRuntimeException.builder().title("In mindestens einem der angegebenen Kanäle gibt es bereits ein Event.").build();
		}

		filteredInformationDtos.forEach(informationDto -> {
			final EventDiscordInformation discordInformation = discordInformationAssembler.fromDto(informationDto);
			discordInformation.setEvent(event);
			event.getDiscordInformation().add(discordInformation);
		});
	}

	/**
	 * If there is an event associated with the given channelId in the given guild, it is archived.
	 *
	 * @param guildId to archive event in
	 * @param channelId to remove information for
	 */
	@Async
	public void removeByChannel(long guildId, long channelId) {
		final Optional<Event> optionalEvent = findEventByChannel(channelId);
		if (optionalEvent.isPresent()) {
			final Event event = optionalEvent.get();
			event.archive(guildId);
			eventPublisher.publishEvent(EventArchiveEvent.builder().event(event).guildId(guildId).build());
		}
	}

	/**
	 * Removes the discord information for the given channel and messageId (if present).
	 * If a discord information is removed, the given runnable is executed.
	 *
	 * @param channelId to remove information for
	 * @param messageId to remove information for
	 * @param onRemoval to execute if a {@link EventDiscordInformation} is removed
	 */
	@Async
	public void removeByMessage(long channelId, long messageId, Runnable onRemoval) {
		findEventByChannel(channelId)
				.ifPresent(event -> event.getDiscordInformation().removeIf(information -> {
					final boolean match = information.getInfoMsg() == messageId
							|| information.getSlotListMsgPartOne() == messageId
							|| information.getSlotListMsgPartTwo() == messageId;
					if (match) {
						onRemoval.run();
					}
					return match;
				}));
	}

	/**
	 * Removes all discord information for the given server
	 *
	 * @param guildId to remove information for
	 */
	@Async
	public void removeByGuild(long guildId) {
		discordInformationRepository.deleteByGuildId(guildId);
	}
}
