package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.AbstractEventDto;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

	public Event findById(long eventId) {
		return eventService.findById(eventId);
	}

	public Optional<Event> findByChannel(long channel) {
		return eventService.findOptionalByChannel(channel);
	}

	public Event findByChannelOrThrow(long channel) {
		return findByChannel(channel).orElseThrow(ResourceNotFoundException::new);
	}

	public List<Event> findAllInPast(long guildId) {
		return eventService.findAllInPast(guildId);
	}

	public List<Event> findAllNotAssignedInFuture(long guildId) {
		return eventService.findAllNotAssignedInFuture(guildId);
	}

	public List<Event> findAllForeignNotAssignedInFuture(long guildId) {
		return eventService.findAllForeignNotAssignedInFuture(guildId);
	}

	public void updateDiscordInformation(AbstractEventDto dto) {
		eventService.updateDiscordInformation(dto);
	}

	public void archiveEvent(long eventId, long guildId) {
		eventService.archiveEvent(eventId, guildId);
	}

	public void slot(long channel, int slotNumber, String userId) {
		eventService.slot(eventService.findByChannel(channel), slotNumber, userDtoWithId(userId));
	}

	public void unslot(long channel, String userId) {
		eventService.unslot(eventService.findByChannel(channel), userDtoWithId(userId));
	}

	public void unslot(long channel, int slotNumber) {
		eventService.unslot(channel, slotNumber);
	}

	public void randomSlot(long channel, String userId) {
		eventService.randomSlot(channel, userDtoWithId(userId));
	}

	public void addSlot(long channel, int squadNumber, int slotNumber, String slotName) {
		eventService.addSlot(channel, squadNumber, SlotDto.builder().number(slotNumber).name(slotName).build());
	}

	public void delSlot(long channel, int slotNumber) {
		eventService.deleteSlot(channel, slotNumber);
	}

	public void blockSlot(long channel, int slotNumber, String replacementText) {
		eventService.blockSlot(channel, slotNumber, replacementText);
	}

	public void renameSquad(long channel, int slotNumber, String squadName) {
		eventService.renameSquad(channel, slotNumber, squadName);
	}

	public void renameSlot(long channel, int slotNumber, String slotName) {
		eventService.renameSlot(channel, slotNumber, slotName);
	}

	public List<Slot> findSwapSlots(long channel, String firstUserId, String secondUserId) {
		return eventService.findSwapSlots(channel, List.of(userDtoWithId(firstUserId), userDtoWithId(secondUserId)));
	}

	public List<Slot> findSwapSlots(long channel, int slotNumber, String userId) {
		return eventService.findSwapSlots(channel, slotNumber, userDtoWithId(userId));
	}

	public List<User> findAllParticipants(long channel) {
		return eventService.findAllParticipants(channel);
	}

	private static UserDto userDtoWithId(String userId) {
		return UserDto.builder().id(userId).build();
	}
}
