package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.EventDiscordInformation;
import de.webalf.slotbot.model.dtos.EventDiscordInformationDto;
import de.webalf.slotbot.repository.EventDiscordInformationRepository;
import de.webalf.slotbot.util.DtoUtils;
import de.webalf.slotbot.util.LongUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * @author Alf
 * @since 04.07.2021
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventDiscordInformationService {
	private final EventDiscordInformationRepository discordInformationRepository;

	/**
	 * Updates discord information with the given values identified by its id (channel). If no information can be found,
	 * new ones are created
	 * (!) Event can not be changed
	 *
	 * @param dto   with new values
	 * @param event is required when a new field must be created
	 * @return updated or new discord information
	 */
	public EventDiscordInformation updateOrCreateDiscordInformation(@NonNull EventDiscordInformationDto dto, @NonNull Event event) {
		Optional<EventDiscordInformation> oldDiscordInformation = Optional.empty();
		if (event.getDiscordInformation() != null) {
			oldDiscordInformation = discordInformationRepository.findById(Long.parseLong(dto.getChannel()));
		}
		final EventDiscordInformation discordInformation = oldDiscordInformation
				.orElseGet(() -> EventDiscordInformation.builder().channel(Long.parseLong(dto.getChannel())).event(event).build());

		DtoUtils.ifPresentParse(dto.getInfoMsg(), discordInformation::setInfoMsg);
		DtoUtils.ifPresentParse(dto.getSlotListMsgPartOne(), discordInformation::setSlotListMsgPartOne);
		DtoUtils.ifPresentParse(dto.getSlotListMsgPartTwo(), discordInformation::setSlotListMsgPartTwo);

		return discordInformation;
	}

	public Optional<Event> findEventByChannel(String channel) {
		return findEventByChannel(LongUtils.parseLong(channel));
	}

	public Optional<Event> findEventByChannel(long channel) {
		return discordInformationRepository.findById(channel)
				.map(EventDiscordInformation::getEvent);
	}
}
