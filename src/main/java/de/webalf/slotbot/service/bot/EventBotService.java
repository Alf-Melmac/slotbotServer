package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.service.EventService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static de.webalf.slotbot.util.bot.MessageUtils.replyAndDelete;

/**
 * Wrapper for {@link EventService} to be used by discord bot
 *
 * @author Alf
 * @since 04.01.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventBotService {
	private final EventService eventService;

	public Optional<Event> findById(@NonNull Message message, long eventId) {
		final Optional<Event> optionalEvent = eventService.findOptionalById(eventId);
		if (optionalEvent.isPresent()) {
			return optionalEvent;
		} else {
			replyAndDelete(message, "Das Event mit der ID " + eventId + "konnte nicht gefunden werden.");
			return Optional.empty();
		}
	}

	public Optional<Event> findByChannel(@NonNull Message message, long channel) {
		final Optional<Event> optionalEvent = eventService.findOptionalByChannel(channel);
		if (optionalEvent.isPresent()) {
			return optionalEvent;
		} else {
			replyAndDelete(message, "Hier konnte kein Event gefunden werden.");
			return Optional.empty();
		}
	}

	public void updateEvent(EventDto dto) {
		eventService.updateEvent(dto);
	}

	public void slot(long channel, int slotNumber, String userId) {
		eventService.slot(channel, slotNumber, userDtoWithId(userId));
	}

	public void unslot(long channel, String userId) {
		eventService.unslot(channel, userDtoWithId(userId));
	}

	public void unslot(long channel, int slotNumber) {
		eventService.unslot(channel, slotNumber);
	}

	private static UserDto userDtoWithId(String userId) {
		return UserDto.builder().id(userId).build();
	}
}
