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

import javax.validation.constraints.NotBlank;
import java.util.List;

import static de.webalf.slotbot.util.ListUtils.oneArgument;
import static de.webalf.slotbot.util.StringUtils.onlyNumbers;
import static de.webalf.slotbot.util.bot.InteractionUtils.finishedSlashCommandAction;
import static de.webalf.slotbot.util.bot.MentionUtils.getId;
import static de.webalf.slotbot.util.bot.MentionUtils.isUserMention;
import static de.webalf.slotbot.util.bot.MessageUtils.deleteMessagesInstant;
import static de.webalf.slotbot.util.bot.MessageUtils.replyAndDelete;
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getIntegerOption;
import static de.webalf.slotbot.util.bot.SlashCommandUtils.getOptionalUserOption;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.EVENT_MANAGE;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.NONE;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.isAuthorized;

/**
 * @author Alf
 * @since 10.01.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Command(names = {"slot", "forceSlot"},
		description = "Slottet dich selbst oder jemand anderen.",
		usage = "<Slotnummer> (<@ZuSlottendePerson>)",
		argCount = {1, 2},
		authorization = NONE)
@SlashCommand(name = "slot",
		description = "Slottet dich in ein Event.",
		authorization = NONE,
		optionPosition = 0)
@SlashCommand(name = "forceSlot",
		description = "Slottet jemand anderen in ein Event.",
		authorization = EVENT_MANAGE,
		optionPosition = 1)
public class Slot implements DiscordCommand, DiscordSlashCommand {
	private final EventBotService eventBotService;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: slot");

		final String slot = args.get(0);
		if (!onlyNumbers(slot)) {
			replyAndDelete(message, "Bitte übergebe an erster Stelle eine Slotnummer.");
			return;
		}

		if (oneArgument(args)) { //Self slot
			selfSlot(message, slot);
		} else { //Slot others
			final String secondArg = args.get(1);
			final boolean isUserMention = isUserMention(secondArg);
			final String userId = getId(secondArg);

			if (isUserMention && message.getAuthor().getId().equals(userId)) { //Message author mentioned himself
				selfSlot(message, slot);
				return;
			}

			if (isAuthorized(EVENT_MANAGE, message)) {
				if (!isUserMention) {
					replyAndDelete(message, "Bitte übergebe nach der Slotnummer eine Person.");
					return;
				}

				slot(message, slot, userId);
			} else {
				replyAndDelete(message, "Du darfst keine anderen Personen slotten.");
			}
		}
	}

	private void slot(@NonNull Message message, @NotBlank String slot, String userId) {
		eventBotService.slot(message.getChannel().getIdLong(), Integer.parseInt(slot), userId);
		deleteMessagesInstant(message);
	}

	private void selfSlot(@NonNull Message message, @NotBlank String slot) {
		slot(message, slot, message.getAuthor().getId());
	}

	private static final String OPTION_SLOT_NUMBER = "slotnummer";
	private static final String OPTION_SLOT_USER = "user";
	private static final List<List<OptionData>> OPTIONS = List.of(
			List.of(new OptionData(OptionType.INTEGER, OPTION_SLOT_NUMBER, "Nummer des erwünschten Slots.", true)),
			List.of(new OptionData(OptionType.INTEGER, OPTION_SLOT_NUMBER, "Nummer des erwünschten Slots.", true),
					new OptionData(OptionType.USER, OPTION_SLOT_USER, "Zu slottende Person.", true))
	);

	@Override
	public void execute(SlashCommandEvent event) {
		log.trace("Slash command: slot");

		@SuppressWarnings("ConstantConditions") //Required option
		final int slotNumber = getIntegerOption(event.getOption(OPTION_SLOT_NUMBER));

		final Long user = getOptionalUserOption(event.getOption(OPTION_SLOT_USER));
		if (user == null) { //Self slot
			eventBotService.slot(event.getChannel().getIdLong(), slotNumber, event.getUser().getId());
		} else { //Slot others
			eventBotService.slot(event.getChannel().getIdLong(), slotNumber, Long.toString(user));
		}

		finishedSlashCommandAction(event);
	}

	@Override
	public List<OptionData> getOptions(int optionPosition) {
		return OPTIONS.get(optionPosition);
	}
}
