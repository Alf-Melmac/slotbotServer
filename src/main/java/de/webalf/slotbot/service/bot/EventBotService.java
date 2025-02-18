package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.EventDiscordInformationDto;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.model.event.EventArchiveInitializedEvent;
import de.webalf.slotbot.service.EventService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import org.hibernate.Hibernate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static net.dv8tion.jda.api.interactions.components.selections.SelectMenu.OPTIONS_MAX_AMOUNT;

/**
 * Wrapper for {@link EventService} to be used by discord bot
 *
 * @author Alf
 * @since 04.01.2021
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EventBotService {
	private final EventService eventService;
	private final GuildBotService guildBotService;
	private final ApplicationEventPublisher eventPublisher;

	public Event findById(long eventId) {
		final Event event = eventService.findById(eventId);
		//Manually initialize requirements for later usage outside the transaction.
		// Every other solution I tried , e.g. EntityGraph, produced duplicate slots or didn't work
		Hibernate.initialize(event.getRequirements());
		event.getSquadList().forEach(squad -> {
			Hibernate.initialize(squad.getRequirements());
			squad.getSlotList().forEach(slot -> Hibernate.initialize(slot.getRequirements()));
		});
		return event;
	}

	public Optional<Event> findByChannel(long channel) {
		return eventService.findOptionalByChannel(channel);
	}

	public Event findByChannelOrThrow(long channel) {
		return eventService.findByChannel(channel);
	}

	public List<Event> findNotAssignedInFutureForSelect(long guildId) {
		return eventService.findNotAssignedInFutureForSelect(guildId, OPTIONS_MAX_AMOUNT);
	}

	public List<Event> findForeignNotAssignedInFutureForSelect(long guildId) {
		return eventService.findForeignNotAssignedInFutureForSelect(guildId, OPTIONS_MAX_AMOUNT);
	}

	public Optional<Event> findLastEventOfUser(User user, de.webalf.slotbot.model.Guild guild) {
		return eventService.findLastEventOfUserInGuild(user, guild);
	}

	public void addDiscordInformation(long eventId, EventDiscordInformationDto dto) {
		eventService.addDiscordInformation(eventId, dto);
	}

	public void archiveEvent(Guild guild, long channel) {
		guildBotService.archiveByChannel(guild.getIdLong(), channel);
	}

	public void retriggerArchiveEvents(Guild guild) {
		final de.webalf.slotbot.model.Guild persistentGuild = guildBotService.find(guild.getIdLong());
		eventService.findAllInPast(persistentGuild)
				.forEach(event -> eventPublisher.publishEvent(EventArchiveInitializedEvent.builder()
						.event(event)
						.guild(persistentGuild)
						.discordGuild(guild)
						.build()));
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

	public void addSlot(long channel, int squadNumber, Integer slotNumber, String slotName) {
		eventService.addSlot(findByChannelOrThrow(channel), squadNumber, slotNumber, slotName);
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

	public List<Long> findAllParticipants(long channel) {
		return eventService.findAllParticipantIds(channel);
	}

	private static UserDto userDtoWithId(String userId) {
		return UserDto.builder().id(userId).build();
	}
}
