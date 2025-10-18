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
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;
import java.util.stream.Collectors;

import static de.webalf.slotbot.util.StringUtils.splitEveryNth;
import static de.webalf.slotbot.util.bot.InteractionUtils.finishedVisibleInteraction;
import static de.webalf.slotbot.util.bot.InteractionUtils.reply;
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

		final MessageChannelUnion eventChannel = event.getChannel();
		if (!eventChannel.canTalk()) {
			reply(event, locale.t("bot.interaction.response.cannotTalk"));
			return;
		}

		final String mentions = eventBotService.findAllParticipants(eventChannel.getIdLong()).stream()
				.map(userId -> User.fromId(userId).getAsMention())
				.collect(Collectors.joining(" "));

		final String message = getStringOption(event, OPTION_MESSAGE);

		final List<String> splitMessages = splitEveryNth(message + "\n" + mentions, Message.MAX_CONTENT_LENGTH);
		for (int i = 0; i < splitMessages.size(); i++) {
			final String messagePart = splitMessages.get(i);
			if (i != splitMessages.size() - 1) {
				sendMessage(event, messagePart);
			} else {
				// Add ping author to last message
				sendMessage(event,
						messagePart,
						Button.secondary("author", locale.t("bot.slash.event.ping.author") + event.getInteraction().getUser().getName())
								.asDisabled());
			}
		}

		finishedVisibleInteraction(event);
	}

	@Override
	public List<TranslatableOptionData> getOptions(int optionPosition) {
		return OPTIONS.get(optionPosition);
	}
}
