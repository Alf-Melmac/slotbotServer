package de.webalf.slotbot.service.bot.command;

import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.service.bot.EventBotService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static de.webalf.slotbot.util.ListUtils.zeroArguments;
import static de.webalf.slotbot.util.PermissionHelper.Authorization.EVENT_MANAGE;
import static de.webalf.slotbot.util.PermissionHelper.Authorization.SLOT;
import static de.webalf.slotbot.util.PermissionHelper.isAuthorized;
import static de.webalf.slotbot.util.bot.MentionUtils.getUserId;
import static de.webalf.slotbot.util.bot.MentionUtils.isUserMention;
import static de.webalf.slotbot.util.bot.MessageUtils.deleteMessagesInstant;
import static de.webalf.slotbot.util.bot.MessageUtils.replyAndDelete;

/**
 * @author Alf
 * @since 11.01.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Command(name = "unslot",
		description = "Slottet dich selbst oder jemand anderen aus.",
		usage = "(<@AuzuslottendePerson>)",
		argCount = {0, 1},
		authorization = SLOT)
public class Unslot implements DiscordCommand {
	private final EventBotService eventBotService;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: unslot");

		if (zeroArguments(args)) { //Self unslot
			selfUnslot(message);
		} else { //Unslot others
			final String secondArg = args.get(1);
			final boolean isUserMention = isUserMention(secondArg);
			final String userId = getUserId(secondArg);

			if (isUserMention && message.getAuthor().getId().equals(userId)) { //Message author mentioned himself
				selfUnslot(message);
				return;
			}

			if (isAuthorized(EVENT_MANAGE, message)) {
				if (isUserMention) { //Unslot via mention
					unslot(message, userId);
					return;
				}

				eventBotService.unslot(message.getChannel().getIdLong(), secondArg); //Unslot via slot number
			} else {
				replyAndDelete(message, "Du darfst keine anderen Personen slotten.");
			}
		}
	}

	private void unslot(@NonNull Message message, String userId) {
		eventBotService.unslot(message.getChannel().getIdLong(), userId);
		deleteMessagesInstant(message);
	}

	private void selfUnslot(@NonNull Message message) {
		unslot(message, message.getAuthor().getId());
	}
}
