package de.webalf.slotbot.service.bot.command;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.util.bot.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Consumer;

import static de.webalf.slotbot.util.PermissionHelper.Authorization.EVENT_MANAGE;

/**
 * @author Alf
 * @since 04.01.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Command(name = "addEventToChannel",
		description = "Ordnet einem Event den aktuellen Kanal zu.",
		usage = "<Event-ID>",
		argCount = {1},
		authorization = EVENT_MANAGE)
public class AddEventToChannel implements DiscordCommand {
	private final EventBotService eventBotService;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: addEventToChannel");

		eventBotService.findById(message, Long.parseLong(args.get(0)))
				.ifPresent(addEventConsumer(message));
	}

	private Consumer<Event> addEventConsumer(@NonNull Message message) {
		return event -> {
			eventBotService.updateEvent(EventDto.builder().id(event.getId()).channel(message.getChannel().getId()).build());
			MessageUtils.replyAndDelete(message, "Event " + event.getName() + " dem aktuellen Kanal hinzugefügt.");
		};
	}
}
