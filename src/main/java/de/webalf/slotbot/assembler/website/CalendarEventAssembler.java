package de.webalf.slotbot.assembler.website;

import de.webalf.slotbot.controller.website.EventWebController;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.ShortEventInformationDto;
import de.webalf.slotbot.model.dtos.website.CalendarEventDto;
import de.webalf.slotbot.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 24.10.2020
 */
@Component
public final class CalendarEventAssembler {
	private static CalendarEventDto toDto(Event event) {
		return CalendarEventDto.builder()
				.title(event.getName())
				.start(event.getDateTime())
				.description(convertShortInformation(event.getShortInformation()))
				.url(linkTo(methodOn(EventWebController.class).getEventDetailsHtml(event.getId())).toUri().toString())
				.build();
	}

	public static List<CalendarEventDto> toDtoList(Iterable<? extends Event> content) {
		return StreamSupport.stream(content.spliterator(), false)
				.map(CalendarEventAssembler::toDto)
				.collect(Collectors.toList());
	}

	private static final String BOLD = "<b>";
	private static final String BOLD_CLOSE = "</b>";
	private static final String BREAK = "<br>";

	/**
	 * Converts a {@link ShortEventInformationDto} to a displayable html text
	 *
	 * @param shortInformation to display
	 * @return html formatted text to display
	 */
	private static String convertShortInformation(ShortEventInformationDto shortInformation) {
		final int emptySlotsCount = shortInformation.getEmptySlotsCount();

		StringBuilder s = new StringBuilder();

		if (emptySlotsCount > 0) {
			s.append(BOLD);
		}
		s.append(emptySlotsCount)
				.append("/")
				.append(shortInformation.getSlotCount());
		if (emptySlotsCount > 0) {
			s.append(BOLD_CLOSE);
		}
		s.append(" Slots frei");

		if (shortInformation.getEmptyReserveSlotsCount() > 0) {
			s.append(BREAK)
					.append(BOLD)
					.append(shortInformation.getEmptyReserveSlotsCount())
					.append(BOLD_CLOSE)
					.append(" Reservistenplätze frei");
		}

		if (StringUtils.isNotEmpty(shortInformation.getMissionLength())) {
			s.append(BREAK)
					.append(BOLD)
					.append("Dauer: ")
					.append(BOLD_CLOSE)
					.append(shortInformation.getMissionLength());
		}

		return s.toString();
	}
}
