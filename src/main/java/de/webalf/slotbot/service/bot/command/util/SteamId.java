package de.webalf.slotbot.service.bot.command.util;

import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.service.bot.UserBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static de.webalf.slotbot.util.ListUtils.zeroArguments;
import static de.webalf.slotbot.util.PermissionHelper.Authorization.NONE;
import static de.webalf.slotbot.util.bot.MessageUtils.*;

/**
 * @author Alf
 * @since 22.02.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Command(names = {"steamId", "steamId64", "setSteamId", "setSteamId64"},
		description = "Gibt die gesetzte Steam-ID aus oder setzt die eigene. Diese wird für Anbindungen an andere Systeme verwendet.",
		usage = "(<steamId64>)",
		argCount = {0, 1},
		authorization = NONE,
		dmAllowed = true)
public class SteamId implements DiscordCommand {
	private final UserBotService userBotService;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: setSteamId");

		if (zeroArguments(args)) { //Get steamId
			final Long steamId = userBotService.findUser(message.getAuthor().getIdLong()).getSteamId64();

			if (steamId == null || steamId == 0) {
				replyAndDeleteOnlySend(message, "Du hast deine Steam-ID noch nicht gesetzt.");
			} else {
				sendDm(message.getAuthor(), "Deine eingetragen Steam-ID ist: `" + steamId + "`");
			}
		} else { //Set steamId
			userBotService.updateUser(UserDto.builder().id(message.getAuthor().getId()).steamId64(args.get(0)).build());
		}

		deleteMessagesInstant(message);
	}
}
