package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.model.dtos.EventDiscordInformationDto;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Consumer;

import static de.webalf.slotbot.util.StringUtils.onlyNumbers;
import static de.webalf.slotbot.util.bot.MessageUtils.replyAndDelete;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.EVENT_MANAGE;

/**
 * @author Alf
 * @since 04.01.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Command(names = {"addEventToChannel", "addChannel", "addEvent"},
		description = "Ordnet einem Event den aktuellen Kanal zu.",
		usage = "<Event-ID>",
		argCount = {1},
		authorization = EVENT_MANAGE)
public class AddEventToChannel implements DiscordCommand {
	private final EventBotService eventBotService;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: addEventToChannel");

		final String eventId = args.get(0);
		if (!onlyNumbers(eventId)) {
			replyAndDelete(message, "Bitte übergebe an erster Stelle eine Event-ID.");
			return;
		}

		eventBotService.findById(message, Long.parseLong(args.get(0)))
				.ifPresent(addEventConsumer(message));
	}

	private Consumer<Event> addEventConsumer(@NonNull Message message) {
		return event -> {
			if (event.isAssigned()) {
				replyAndDelete(message, "Das Event ist bereits " + event.getDiscordInformation().getChannelAsMention() + " zugeordnet.");
				return;
			}

			eventBotService.updateEvent(
					EventDto.builder()
							.id(event.getId())
							.discordInformation(EventDiscordInformationDto.builder().channel(message.getChannel().getId()).build())
							.build());
			replyAndDelete(message, "Event " + event.getName() + " dem aktuellen Kanal hinzugefügt.");
		};
	}
}
