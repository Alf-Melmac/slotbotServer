package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.assembler.api.EventApiAssembler;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.api.EventApiDto;
import de.webalf.slotbot.util.EventUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Alf
 * @since 11.01.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventUpdateService {
	private final BotService botService;

	public void update(Event event) throws IllegalStateException {
		log.trace("Update");

		final TextChannel eventChannel = botService.getJda().getTextChannelById(event.getChannel());
		if (eventChannel == null) {
			throw new IllegalStateException("Channel " + event.getChannel() + "couldn't be found.");
		}

		final EventApiDto eventApiDto = EventApiAssembler.toDto(event);
		eventChannel.editMessageById(event.getInfoMsg(), EventUtils.buildDetailsEmbed(eventApiDto)).queue();
		eventChannel.editMessageById(event.getSlotListMsg(), eventApiDto.getSlotList()).queue();
	}
}
