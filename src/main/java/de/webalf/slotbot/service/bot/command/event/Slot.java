package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import static de.webalf.slotbot.util.ListUtils.oneArgument;
import static de.webalf.slotbot.util.PermissionHelper.Authorization.EVENT_MANAGE;
import static de.webalf.slotbot.util.PermissionHelper.Authorization.SLOT;
import static de.webalf.slotbot.util.PermissionHelper.isAuthorized;
import static de.webalf.slotbot.util.StringUtils.onlyNumbers;
import static de.webalf.slotbot.util.bot.MentionUtils.getUserId;
import static de.webalf.slotbot.util.bot.MentionUtils.isUserMention;
import static de.webalf.slotbot.util.bot.MessageUtils.deleteMessagesInstant;
import static de.webalf.slotbot.util.bot.MessageUtils.replyAndDelete;

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
		authorization = SLOT)
public class Slot implements DiscordCommand {
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
			final String userId = getUserId(secondArg);

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

	private void slot(@NotNull Message message, @NotBlank String slot, String userId) {
		eventBotService.slot(message.getChannel().getIdLong(), Integer.parseInt(slot), userId);
		deleteMessagesInstant(message);
	}

	private void selfSlot(@NotNull Message message, @NotBlank String slot) {
		slot(message, slot, message.getAuthor().getId());
	}
}
