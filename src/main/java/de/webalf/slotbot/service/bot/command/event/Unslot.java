package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.model.annotations.SlashCommand;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static de.webalf.slotbot.util.ListUtils.zeroArguments;
import static de.webalf.slotbot.util.StringUtils.onlyNumbers;
import static de.webalf.slotbot.util.bot.InteractionUtils.finishedSlashCommandAction;
import static de.webalf.slotbot.util.bot.MentionUtils.getId;
import static de.webalf.slotbot.util.bot.MentionUtils.isUserMention;
import static de.webalf.slotbot.util.bot.MessageUtils.*;
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getOptionalIntegerOption;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.EVENT_MANAGE;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.NONE;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.isAuthorized;

/**
 * @author Alf
 * @since 11.01.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Command(names = {"unslot", "forceUnslot"},
		description = "Slottet dich selbst oder jemand anderen aus.",
		usage = "(<@AuzuslottendePerson>)",
		argCount = {0, 1},
		authorization = NONE)
@SlashCommand(name = "unslot",
		description = "Slottet dich aus einem Event aus.",
		authorization = NONE)
@SlashCommand(name = "forceUnslot",
		description = "Slottet jemand anderen aus einem Event aus.",
		authorization = EVENT_MANAGE,
		optionPosition = 0)
public class Unslot implements DiscordCommand, DiscordSlashCommand {
	private final EventBotService eventBotService;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: unslot");

		if (zeroArguments(args)) { //Self unslot
			selfUnslot(message);
		} else { //Unslot others
			final String secondArg = args.get(0);
			final boolean isUserMention = isUserMention(secondArg);
			final String userId = getId(secondArg);

			if (isUserMention && message.getAuthor().getId().equals(userId)) { //Message author mentioned himself
				selfUnslot(message);
				return;
			}

			if (isAuthorized(EVENT_MANAGE, message)) {
				if (isUserMention) { //Unslot via mention
					unslot(message, userId);
				} else {
					unslotViaSlotNumber(message, secondArg);
				}
				deleteMessagesInstant(message);
			} else {
				replyAndDelete(message, "Du darfst keine anderen Personen ausslotten.");
			}
		}
	}

	private void unslot(@NonNull Message message, String userId) {
		eventBotService.unslot(message.getChannel().getIdLong(), userId);
	}

	private void unslotViaSlotNumber(Message message, String secondArg) {
		if (onlyNumbers(secondArg)) {
			eventBotService.unslot(message.getChannel().getIdLong(), Integer.parseInt(secondArg));
		} else {
			replyAndDeleteOnlySend(message, "Bitte übergebe an erster Stelle eine Slotnummer oder die auszuslottende Person.");
		}
	}

	private void selfUnslot(@NonNull Message message) {
		unslot(message, message.getAuthor().getId());
		deleteMessagesInstant(message);
	}

	private static final String OPTION_SLOT_NUMBER = "slotnummer";
	private static final List<List<OptionData>> OPTIONS = List.of(
			List.of(new OptionData(OptionType.INTEGER, OPTION_SLOT_NUMBER, "Nummer des zu leerenden Slots.", true))
	);

	@Override
	public void execute(SlashCommandEvent event) {
		log.trace("Slash command: unslot");

		final Integer slotNumber = getOptionalIntegerOption(event.getOption(OPTION_SLOT_NUMBER));
		if (slotNumber == null) { //Self unslot
			eventBotService.unslot(event.getChannel().getIdLong(), event.getUser().getId());
		} else { //Unslot others
			eventBotService.unslot(event.getChannel().getIdLong(), slotNumber);
		}

		finishedSlashCommandAction(event);
	}

	@Override
	public List<OptionData> getOptions(int optionPosition) {
		return OPTIONS.get(optionPosition);
	}
}
