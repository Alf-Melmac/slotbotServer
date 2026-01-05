package de.webalf.slotbot.assembler.referenceless;

import de.webalf.slotbot.assembler.EventFieldAssembler;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.EventField;
import de.webalf.slotbot.model.dtos.referenceless.EventFieldReferencelessDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * @author Alf
 * @since 08.06.2023
 */
@Component
@RequiredArgsConstructor
public class EventFieldReferencelessAssembler {
	private final MessageSource messageSource;

	public List<EventFieldReferencelessDto> toDtoList(@NonNull Event event) {
		return getDetails(event.getDetails(), event.getReserveParticipating(), event.getOwnerGuildLocale());
	}

	private List<EventFieldReferencelessDto> getDetails(List<EventField> details, Boolean reserveParticipating, @NonNull Locale guildLocale) {
		final List<EventFieldReferencelessDto> detailDtos = EventFieldAssembler.toReferencelessDtoList(details);
		if (reserveParticipating != null) {
			detailDtos.add(EventFieldReferencelessDto.builder()
					.title(messageSource.getMessage("event.details.reserveParticipating", null, guildLocale))
					.textAsHtml(reserveParticipating.toString())
					.build());
		}
		detailDtos.forEach(detailDto -> {
			final String detailText = detailDto.getTextAsHtml();
			if (detailText.equals("true")) {
				detailDto.setTextAsHtml(messageSource.getMessage("yes", null, guildLocale));
			} else if (detailText.equals("false")) {
				detailDto.setTextAsHtml(messageSource.getMessage("no", null, guildLocale));
			}
		});
		return detailDtos;
	}
}
