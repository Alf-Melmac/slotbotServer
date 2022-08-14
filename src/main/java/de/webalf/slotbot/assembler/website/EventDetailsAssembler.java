package de.webalf.slotbot.assembler.website;

import de.webalf.slotbot.assembler.EventFieldAssembler;
import de.webalf.slotbot.assembler.EventTypeAssembler;
import de.webalf.slotbot.assembler.GuildAssembler;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.EventField;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.model.dtos.referenceless.EventFieldReferencelessDto;
import de.webalf.slotbot.model.dtos.website.EventDetailsDto;
import de.webalf.slotbot.model.dtos.website.EventDetailsSlotDto;
import de.webalf.slotbot.model.dtos.website.EventDetailsSquadDto;
import de.webalf.slotbot.model.dtos.website.EventEditDto;
import de.webalf.slotbot.service.external.DiscordApiService;
import de.webalf.slotbot.util.DateUtils;
import de.webalf.slotbot.util.DiscordMarkdown;
import de.webalf.slotbot.util.LongUtils;
import de.webalf.slotbot.util.StringUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static de.webalf.slotbot.service.GuildService.getLogo;

/**
 * @author Alf
 * @since 30.10.2020
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventDetailsAssembler {
	private final DiscordApiService discordApiService;

	public EventDetailsDto toDto(@NonNull Event event) {
		return toDto(event, true);
	}

	public EventDetailsDto toDto(@NonNull Event event, boolean optimizeReservedFor) {
		final String pictureUrl = event.getPictureUrl();

		return EventDetailsDto.builder()
				.id(event.getId())
				.missionType(event.getMissionType())
				.eventType(EventTypeAssembler.toDto(event.getEventType()))
				.pictureUrl(StringUtils.isNotEmpty(pictureUrl) ? pictureUrl : getLogo(event.getOwnerGuild()))
				.name(event.getName())
				.dateTimeZoned(DateUtils.getDateTimeZoned(event.getDateTime()))
				.missionLength(event.getMissionLength())
				.descriptionAsHtml(DiscordMarkdown.toHtml(event.getDescription()))
				.creator(event.getCreator())
				.squadList(toEventDetailsDtoList(event.getSquadList(), optimizeReservedFor))
				.details(getDetails(event.getDetails(), event.getReserveParticipating()))
				.build();
	}

	public EventEditDto toEditDto(@NonNull Event event) {
		final LocalDateTime dateTime = event.getDateTime();

		return EventEditDto.builder()
				.id(event.getId())
				.hidden(event.isHidden())
				.shareable(event.isShareable())
				.name(event.getName())
				.date(dateTime.toLocalDate())
				.startTime(dateTime.toLocalTime())
				.creator(event.getCreator())
				.eventType(EventTypeAssembler.toDto(event.getEventType()))
				.description(event.getDescription())
				.missionType(event.getMissionType())
				.missionLength(event.getMissionLength())
				.pictureUrl(event.getPictureUrl())
				.details(EventFieldAssembler.toDefaultDtoList(event.getDetails()))
				.squadList(toEventDetailsDtoList(event.getSquadList(), false))
				.reserveParticipating(event.getReserveParticipating())
				.ownerGuild(Long.toString(event.getOwnerGuild().getId()))
				.build();
	}

	private List<EventFieldReferencelessDto> getDetails(List<EventField> details, Boolean reserveParticipating) {
		final List<EventFieldReferencelessDto> detailDtos = EventFieldAssembler.toReferencelessDtoList(details);
		if (reserveParticipating != null) {
			detailDtos.add(EventFieldReferencelessDto.builder().title("Kann die Reserve mitspielen?").text(reserveParticipating.toString()).build());
		}
		detailDtos.forEach(detailDto -> {
			final String detailText = detailDto.getText();
			if (detailText.equals("true")) {
				detailDto.setText("Ja");
			} else if (detailText.equals("false")) {
				detailDto.setText("Nein");
			}
		});
		return detailDtos;
	}

	private List<EventDetailsSquadDto> toEventDetailsDtoList(@NonNull Iterable<? extends Squad> squadList, boolean optimizeReservedFor) {
		return StreamSupport.stream(squadList.spliterator(), false)
				.map(squad -> toEventDetailsDto(squad, optimizeReservedFor))
				.collect(Collectors.toList());
	}

	private EventDetailsSquadDto toEventDetailsDto(@NonNull Squad squad, boolean optimizeReservedFor) {
		final List<EventDetailsSlotDto> slotList = toEventDetailsSlotDtoList(squad.getSlotList(), optimizeReservedFor);
		return EventDetailsSquadDto.builder()
				.id(squad.getId())
				.name(squad.getName())
				.reservedFor(GuildAssembler.toDto(squad.getReservedFor()))
				.slotList(slotList)
				.notEmpty(slotList.stream().anyMatch(EventDetailsSlotDto::isOccupied))
				.build();
	}

	private List<EventDetailsSlotDto> toEventDetailsSlotDtoList(@NonNull Iterable<? extends Slot> slotList, boolean optimizeReservedFor) {
		return StreamSupport.stream(slotList.spliterator(), false)
				.map(slot -> toEventDetailsSlotDto(slot, optimizeReservedFor))
				.collect(Collectors.toList());
	}

	private EventDetailsSlotDto toEventDetailsSlotDto(@NonNull Slot slot, boolean optimizeReservedFor) {
		String text = null;
		boolean blocked = false;
		if (slot.getUser() != null) {
			if (slot.getUser().isDefaultUser()) {
				text = slot.getReplacementText();
				if (StringUtils.isEmpty(text)) {
					text = "Gesperrt";
				}
				blocked = true;
			} else {
				text = discordApiService.getName(LongUtils.toString(slot.getUser().getId()), slot.getSquad().getEvent().getOwnerGuild().getId());
			}
		}

		return EventDetailsSlotDto.builder()
				.id(slot.getId())
				.name(slot.getName())
				.number(slot.getNumber())
				.reservedFor(GuildAssembler.toDto(optimizeReservedFor ? slot.getEffectiveReservedForDisplay() : slot.getReservedFor()))
				.text(text)
				.occupied(!(slot.getUser() == null || slot.getUser().isDefaultUser()))
				.blocked(blocked)
				.build();
	}
}
