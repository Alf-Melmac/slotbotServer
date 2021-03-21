package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import de.webalf.slotbot.util.bot.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.internal.entities.UserById;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.EVENT_MANAGE;

/**
 * @author Alf
 * @since 21.03.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Command(names = {"eventPing", "pingEvent", "@Teilnehmer"},
		description = "Sendet die übergebene Nachricht und pingt dabei alle aktuell geslotteten Personen des Events.",
		usage = "\"<Nachricht>\"",
		argCount = {1},
		authorization = EVENT_MANAGE)
public class EventPing implements DiscordCommand {
	private final EventBotService eventBotService;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: eventPing");

		final String mentions = buildMentions(eventBotService.findAllParticipants(message.getChannel().getIdLong()));
		message.getChannel().sendMessage(args.get(0) + "\n" + mentions).queue();

		MessageUtils.deleteMessagesInstant(message);
	}

	private String buildMentions(Collection<? extends User> users) {
		return users.stream()
				.map(user -> new UserById(user.getId()).getAsMention())
				.collect(Collectors.joining(" "));
	}
}
