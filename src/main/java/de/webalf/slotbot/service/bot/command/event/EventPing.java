package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.annotations.bot.SlashCommand;
import de.webalf.slotbot.model.bot.TranslatableOptionData;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import de.webalf.slotbot.util.bot.DiscordLocaleHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;
import java.util.stream.Collectors;

import static de.webalf.slotbot.util.bot.InteractionUtils.finishedVisibleInteraction;
import static de.webalf.slotbot.util.bot.MessageUtils.sendMessage;
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getStringOption;

/**
 * @author Alf
 * @since 21.03.2021
 */
@RequiredArgsConstructor
@Slf4j
@SlashCommand(name = "bot.slash.event.ping",
		description = "bot.slash.event.ping.description",
		authorization = Permission.MESSAGE_HISTORY,
		optionPosition = 0)
public class EventPing implements DiscordSlashCommand {
	private final EventBotService eventBotService;

	private static final String OPTION_MESSAGE = "bot.slash.event.ping.option.message";
	private static final List<List<TranslatableOptionData>> OPTIONS = List.of(
			List.of(new TranslatableOptionData(OptionType.STRING, OPTION_MESSAGE, "bot.slash.event.ping.option.message.description", true))
	);

	@Override
	public void execute(@NonNull SlashCommandInteractionEvent event, @NonNull DiscordLocaleHelper locale) {
		log.trace("Slash command: eventPing");

		final String mentions = eventBotService.findAllParticipants(event.getChannel().getIdLong()).stream()
				.map(userId -> User.fromId(userId).getAsMention())
				.collect(Collectors.joining(" "));

		final String message = getStringOption(event, OPTION_MESSAGE);

		sendMessage(event, message + "\n" + mentions);

		finishedVisibleInteraction(event);
	}

	@Override
	public List<TranslatableOptionData> getOptions(int optionPosition) {
		return OPTIONS.get(optionPosition);
	}
}
