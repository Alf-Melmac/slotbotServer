package de.webalf.slotbot.assembler.website;

import de.webalf.slotbot.assembler.EventTypeAssembler;
import de.webalf.slotbot.assembler.GuildAssembler;
import de.webalf.slotbot.assembler.referenceless.EventFieldReferencelessAssembler;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.website.EventDetailsDto;
import de.webalf.slotbot.model.dtos.website.EventDetailsSlotDto;
import de.webalf.slotbot.model.dtos.website.EventDetailsSquadDto;
import de.webalf.slotbot.service.UserService;
import de.webalf.slotbot.service.external.DiscordApiService;
import de.webalf.slotbot.util.DateUtils;
import de.webalf.slotbot.util.DiscordMarkdown;
import de.webalf.slotbot.util.LongUtils;
import de.webalf.slotbot.util.permissions.PermissionHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

import static de.webalf.slotbot.util.permissions.PermissionHelper.isLoggedInUser;

/**
 * @author Alf
 * @since 30.10.2020
 */
@Component
@RequiredArgsConstructor
public class EventDetailsAssembler {
	private final DiscordApiService discordApiService;
	private final EventFieldReferencelessAssembler eventFieldReferencelessAssembler;
	private final UserService userService;

	public EventDetailsDto toDto(@NonNull Event event) {
		return EventDetailsDto.builder()
				.id(event.getId())
				.hidden(event.isHidden())
				.missionType(event.getMissionType())
				.eventType(EventTypeAssembler.toDto(event.getEventType()))
				.pictureUrl(event.getPictureUrlWithDefault())
				.name(event.getName())
				.dateTime(event.getDateTime())
				.missionLength(event.getMissionLength())
				.descriptionAsHtml(DiscordMarkdown.toHtml(event.getDescription()))
				.creator(event.getCreator())
				.squadList(toEventDetailsDtoList(event.getSquadList()))
				.details(eventFieldReferencelessAssembler.toDtoList(event))
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
				.notEmpty(slotList.stream().anyMatch(EventDetailsSlotDto::isOccupied))
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
		boolean occupied = false;
		boolean blocked = false;
		if (user != null) {
			if (user.isDefaultUser()) {
				text = slot.getReplacementTextOrDefault();
				blocked = true;
			} else {
				text = discordApiService.getName(LongUtils.toString(user.getId()), slot.getSquad().getEvent().getOwnerGuild().getId());
				occupied = true;
			}
		}

		return EventDetailsSlotDto.builder()
				.id(slot.getId())
				.name(slot.getName())
				.number(slot.getNumber())
				.reservedFor(GuildAssembler.toDto(slot.getEffectiveReservedForDisplay()))
				.text(text)
				.occupied(occupied)
				.blocked(blocked)
				.own(user != null && isLoggedInUser(user.getId()))
				.slottable(PermissionHelper.getLoggedIn() == null || !DateUtils.isInFuture(slot.getEvent().getDateTime()) ? null : slot.slotIsPossible(userService.getLoggedIn()))
				.build();
	}
}
