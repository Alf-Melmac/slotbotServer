package de.webalf.slotbot.assembler.website;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.website.EventDetailsDto;
import de.webalf.slotbot.model.dtos.website.EventDetailsSlotDto;
import de.webalf.slotbot.model.dtos.website.EventDetailsSquadDto;
import de.webalf.slotbot.service.external.DiscordApiService;
import de.webalf.slotbot.util.EventUtils;
import de.webalf.slotbot.util.LongUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 30.10.2020
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventDetailsAssembler {
	private final DiscordProperties discordProperties;
	private final DiscordApiService discordApiService;

	public EventDetailsDto toDto(@NonNull Event event) {
		LocalDateTime dateTime = event.getDateTime();
		String channelUrl = "discord://discordapp.com/channels/" + discordProperties.getGuild() + "/" + LongUtils.toString(event.getChannel());

		return EventDetailsDto.builder()
				.id(event.getId())
				.name(event.getName())
				.date(dateTime.toLocalDate())
				.startTime(dateTime.toLocalTime())
				.creator(event.getCreator())
				.hidden(event.isHidden())
				.channelUrl(event.getChannel() != null ? channelUrl : null)
				.squadList(toEventDetailsDtoList(event.getSquadList()))
				.description(event.getDescription())
				.pictureUrl(event.getPictureUrl())
				.missionType(event.getMissionType())
				.respawn(event.getRespawn())
				.missionLength(event.getMissionLength())
				.reserveParticipating(event.getReserveParticipating())
				.modPack(event.getModPack())
				.modPackUrl(EventUtils.getModPackUrl(event.getModPack()))
				.map(event.getMap())
				.missionTime(event.getMissionTime())
				.navigation(event.getNavigation())
				.technicalTeleport(event.getTechnicalTeleport())
				.medicalSystem(event.getMedicalSystem())
				.build();
	}

	private List<EventDetailsSquadDto> toEventDetailsDtoList(@NonNull Iterable<? extends Squad> squadList) {
		return StreamSupport.stream(squadList.spliterator(), false)
				.map(this::toEventDetailsDto)
				.collect(Collectors.toList());
	}

	private EventDetailsSquadDto toEventDetailsDto(@NonNull Squad squad) {
		final List<EventDetailsSlotDto> slotList = toEventDetailsSlotDtoList(squad.getSlotList()).stream().sorted(Comparator.comparing(EventDetailsSlotDto::getNumber)).collect(Collectors.toList());
		return EventDetailsSquadDto.builder()
				.id(squad.getId())
				.name(squad.getName())
				.slotList(slotList)
				.notEmpty(slotList.stream().anyMatch(EventDetailsSlotDto::isOccupied))
				.build();
	}

	private List<EventDetailsSlotDto> toEventDetailsSlotDtoList(@NonNull Iterable<? extends Slot> slotList) {
		return StreamSupport.stream(slotList.spliterator(), false)
				.map(this::toEventDetailsSlotDto)
				.collect(Collectors.toList());
	}

	private EventDetailsSlotDto toEventDetailsSlotDto(@NonNull Slot slot) {
		String text = null;
		boolean blocked = false;
		if (slot.getUser() != null) {
			if (slot.getUser().isDefaultUser()) {
				text = slot.getReplacementText();
				blocked = true;
			} else {
				text = discordApiService.getName(LongUtils.toString(slot.getUser().getId()));
			}
		}

		return EventDetailsSlotDto.builder()
				.id(slot.getId())
				.name(slot.getName())
				.number(slot.getNumber())
				.text(text)
				.occupied(!(slot.getUser() == null || slot.getUser().isDefaultUser()))
				.blocked(blocked)
				.build();
	}
}
