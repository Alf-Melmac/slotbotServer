package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.model.annotations.SlashCommand;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.entities.UserById;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static de.webalf.slotbot.util.bot.InteractionUtils.finishedSlashCommandAction;
import static de.webalf.slotbot.util.bot.MessageUtils.deleteMessagesInstant;
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getStringOption;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.NONE;

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
		authorization = NONE)
@SlashCommand(name = "eventPing",
		description = "Sendet die übergebene Nachricht und pingt dabei alle aktuell geslotteten Personen des Events.",
		authorization = NONE,
		optionPosition = 0)
public class EventPing implements DiscordCommand, DiscordSlashCommand {
	private final EventBotService eventBotService;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: eventPing");

		final String mentions = buildMentions(eventBotService.findAllParticipants(message.getChannel().getIdLong()));
		message.getChannel().sendMessage(args.get(0) + "\n" + mentions).queue();

		deleteMessagesInstant(message);
	}

	private String buildMentions(Collection<? extends User> users) {
		return users.stream()
				.map(user -> new UserById(user.getId()).getAsMention())
				.collect(Collectors.joining(" "));
	}

	private static final String OPTION_MESSAGE = "nachricht";
	private static final List<List<OptionData>> OPTIONS = List.of(
			List.of(new OptionData(OptionType.STRING, OPTION_MESSAGE, "Zu versendender Text.", true))
	);

	@Override
	public void execute(SlashCommandEvent event) {
		log.trace("Slash command: eventPing");

		final String mentions = buildMentions(eventBotService.findAllParticipants(event.getChannel().getIdLong()));

		@SuppressWarnings("ConstantConditions") //Required option
		final String message = getStringOption(event.getOption(OPTION_MESSAGE));

		event.getChannel().sendMessage(message + "\n" + mentions).queue();

		finishedSlashCommandAction(event);
	}

	@Override
	public List<OptionData> getOptions(int optionPosition) {
		return OPTIONS.get(optionPosition);
	}
}
