package de.webalf.slotbot.assembler.website;

import de.webalf.slotbot.assembler.EventTypeAssembler;
import de.webalf.slotbot.assembler.GuildAssembler;
import de.webalf.slotbot.assembler.referenceless.EventFieldReferencelessAssembler;
import de.webalf.slotbot.feature.requirement.RequirementListAssembler;
import de.webalf.slotbot.feature.slot_rules.SlottableAssembler;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.website.EventDetailsDto;
import de.webalf.slotbot.model.dtos.website.EventDetailsSlotDto;
import de.webalf.slotbot.model.dtos.website.EventDetailsSquadDto;
import de.webalf.slotbot.service.SlotService;
import de.webalf.slotbot.service.external.DiscordBotService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Alf
 * @since 30.10.2020
 */
@Component
@RequiredArgsConstructor
public class EventDetailsAssembler {
	private final DiscordBotService discordBotService;
	private final EventFieldReferencelessAssembler eventFieldReferencelessAssembler;
	private final SlotService slotService;

	public EventDetailsDto toDto(@NonNull Event event) {
		return EventDetailsDto.builder()
				.id(event.getId())
				.hidden(event.isHidden())
				.ownerGuildIdentifier(event.getOwnerGuild().getGroupIdentifier())
				.missionType(event.getMissionType())
				.eventType(EventTypeAssembler.toDto(event.getEventType()))
				.pictureUrl(event.getPictureUrlWithDefault())
				.name(event.getName())
				.dateTime(event.getDateTime())
				.missionLength(event.getMissionLength())
				.descriptionAsHtml(event.getDescription())
				.creator(event.getCreator())
				.squadList(toEventDetailsDtoList(event.getSquadList()))
				.details(eventFieldReferencelessAssembler.toDtoList(event))
				.requirements(RequirementListAssembler.toDtoList(event.getRequirements()))
				.build();
	}

	private List<EventDetailsSquadDto> toEventDetailsDtoList(@NonNull Iterable<? extends Squad> squadList) {
		return StreamSupport.stream(squadList.spliterator(), false)
				.map(this::toEventDetailsDto)
				.toList();
	}

	private EventDetailsSquadDto toEventDetailsDto(@NonNull Squad squad) {
		final List<EventDetailsSlotDto> slotList = toEventDetailsSlotDtoList(squad.getSlotList());
		return EventDetailsSquadDto.builder()
				.id(squad.getId())
				.name(squad.getName())
				.reservedFor(GuildAssembler.toDto(squad.getReservedFor()))
				.slotList(slotList)
				.requirements(RequirementListAssembler.toDtoList(squad.getRequirements()))
				.build();
	}

	private List<EventDetailsSlotDto> toEventDetailsSlotDtoList(@NonNull Iterable<? extends Slot> slotList) {
		return StreamSupport.stream(slotList.spliterator(), false)
				.map(this::toEventDetailsSlotDto)
				.toList();
	}

	private EventDetailsSlotDto toEventDetailsSlotDto(@NonNull Slot slot) {
		final User user = slot.getUser();
		String text = null;
		if (user != null) {
			if (user.isDefaultUser()) {
				text = slot.getReplacementTextOrDefault();
			} else {
				text = discordBotService.getName(user.getId(), slot.getSquad().getEvent().getOwnerGuild().getId());
			}
		}

		return EventDetailsSlotDto.builder()
				.id(slot.getId())
				.name(slot.getName())
				.number(slot.getNumber())
				.reservedFor(GuildAssembler.toDto(slot.getEffectiveReservedForDisplay()))
				.text(text)
				.requirements(RequirementListAssembler.toDtoList(slot.getRequirements()))
				.slottable(SlottableAssembler.toDto(slotService.getSlottable(slot)))
				.build();
	}
}
