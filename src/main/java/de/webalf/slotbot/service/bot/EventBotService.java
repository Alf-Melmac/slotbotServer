package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.dtos.EventDiscordInformationDto;
import de.webalf.slotbot.model.dtos.SlotDto;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.service.EventService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Wrapper for {@link EventService} to be used by discord bot
 *
 * @author Alf
 * @since 04.01.2021
 */
@Service
@Transactional
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
		return eventService.findByChannel(channel);
	}

	public List<Event> findAllNotAssignedInFuture(long guildId) {
		return eventService.findAllNotAssignedInFuture(guildId);
	}

	public List<Event> findAllForeignNotAssignedInFuture(long guildId) {
		return eventService.findAllForeignNotAssignedInFuture(guildId);
	}

	public void addDiscordInformation(long eventId, EventDiscordInformationDto dto) {
		eventService.addDiscordInformation(eventId, dto);
	}

	public void archiveEvent(long channel, Guild guild) {
		eventService.archiveEvent(findByChannelOrThrow(channel), guild);
	}

	public void retriggerArchiveEvents(Guild guild) {
		eventService.retriggerArchiveEvents(guild);
	}

	public void slot(long channel, int slotNumber, String userId) {
		eventService.slot(findByChannelOrThrow(channel), slotNumber, Long.parseLong(userId));
	}

	public void blockSlot(long channel, int slotNumber, String replacementText) {
		eventService.blockSlot(findByChannelOrThrow(channel), slotNumber, replacementText);
	}

	public void unslot(long channel, String userId) {
		eventService.unslot(findByChannelOrThrow(channel), userDtoWithId(userId));
	}

	public void unslot(long channel, int slotNumber) {
		eventService.unslot(findByChannelOrThrow(channel), slotNumber);
	}

	public void randomSlot(long channel, String userId) {
		eventService.randomSlot(channel, userDtoWithId(userId));
	}

	public void addSlot(long channel, int squadNumber, int slotNumber, String slotName) {
		eventService.addSlot(findByChannelOrThrow(channel), squadNumber, SlotDto.builder().number(slotNumber).name(slotName).build());
	}

	public void delSlot(long channel, int slotNumber) {
		eventService.deleteSlot(findByChannelOrThrow(channel), slotNumber);
	}

	public void renameSquad(long channel, int slotNumber, String squadName) {
		eventService.renameSquad(findByChannelOrThrow(channel), slotNumber, squadName);
	}

	public void renameSlot(long channel, int slotNumber, String slotName) {
		eventService.renameSlot(findByChannelOrThrow(channel), slotNumber, slotName);
	}

	public List<Slot> findSwapSlots(long channel, String firstUserId, String secondUserId) {
		return eventService.findSwapSlots(findByChannelOrThrow(channel), List.of(userDtoWithId(firstUserId), userDtoWithId(secondUserId)));
	}

	public List<Long> findAllParticipants(long channel) {
		return eventService.findAllParticipantIds(channel);
	}

	private static UserDto userDtoWithId(String userId) {
		return UserDto.builder().id(userId).build();
	}
}
