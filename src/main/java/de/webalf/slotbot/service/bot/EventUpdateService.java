package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.assembler.api.EventApiAssembler;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.api.EventApiDto;
import de.webalf.slotbot.util.EventUtils;
import de.webalf.slotbot.util.ListUtils;
import de.webalf.slotbot.util.bot.MessageHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static de.webalf.slotbot.service.bot.command.event.EventPrint.sendSpacerEmojiIfEmpty;
import static de.webalf.slotbot.util.DateUtils.DATE_FORMATTER;

/**
 * @author Alf
 * @since 11.01.2021
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventUpdateService {
	private final BotService botService;
	private final MessageHelper messageHelper;

	public void update(@NonNull Event event) throws IllegalStateException {
		log.trace("Update");

		final TextChannel eventChannel = botService.getJda().getTextChannelById(event.getDiscordInformation().getChannel());
		if (eventChannel == null) {
			throw new IllegalStateException("Channel " + event.getDiscordInformation().getChannel() + " couldn't be found.");
		}

		final EventApiDto eventApiDto = EventApiAssembler.toDto(event);
		eventChannel.editMessageEmbedsById(event.getDiscordInformation().getInfoMsg(), EventUtils.buildDetailsEmbed(eventApiDto)).queue();
		final List<String> slotList = eventApiDto.getSlotList();
		eventChannel.editMessageById(event.getDiscordInformation().getSlotListMsgPartOne(), ListUtils.shift(slotList)).queue();
		eventChannel.editMessageById(event.getDiscordInformation().getSlotListMsgPartTwo(), sendSpacerEmojiIfEmpty(ListUtils.shift(slotList))).queue();
	}

	public void informAboutSlotChange(@NonNull Event event, @NonNull Slot slot, User currentUser, User previousUser) {
		final String eventDate = DATE_FORMATTER.format(event.getDateTime().toLocalDate());
		if (currentUser != null && !currentUser.isDefaultUser()) {
			messageHelper.sendDmToRecipient(currentUser, "Du bist im Event **" + event.getName() + "** am " + eventDate + " nun auf dem Slot " + slot.getNumber() + " *" + slot.getName() + "* eingetragen.");
			longTimeNoSee(currentUser);
		} else if (previousUser != null && !previousUser.isDefaultUser()) {
			messageHelper.sendDmToRecipient(previousUser, "Du bist nun vom Event **" + event.getName() + "** am " + eventDate + " ausgetragen.");
		}
	}

	/**
	 * If the last event slotting is more than 3 months in the past the user gets an additional message
	 *
	 * @param currentUser user that slots
	 */
	private void longTimeNoSee(@NonNull User currentUser) {
		currentUser.getLastEventDateTime().ifPresent(lastEvent -> {
			if (lastEvent.plusMonths(3).isBefore(LocalDateTime.now())) {
				messageHelper.sendDmToRecipient(currentUser, "Über drei Monate haben wir dich nicht mehr gesehen. Schau doch gerne mal wieder öfter vorbei. Falls du einen neuen Technikcheck brauchst oder andere Fragen hast, melde dich doch bitte bei <@327385716977958913>.");
			}
		});
	}
}
