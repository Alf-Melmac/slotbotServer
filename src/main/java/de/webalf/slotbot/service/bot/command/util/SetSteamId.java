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

import static de.webalf.slotbot.util.PermissionHelper.Authorization.NONE;
import static de.webalf.slotbot.util.bot.MessageUtils.deleteMessagesInstant;

/**
 * @author Alf
 * @since 22.02.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Command(names = {"setSteamId", "setSteamId64"},
		description = "Setzt die eigene Steam-ID. Diese wird für Anbindungen an andere Systeme verwendet.",
		usage = "<steamId64>",
		argCount = {1},
		authorization = NONE,
		dmAllowed = true)
public class SetSteamId implements DiscordCommand {
	private final UserBotService userBotService;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: setSteamId");

		userBotService.updateUser(UserDto.builder().id(message.getAuthor().getId()).steamId64(args.get(0)).build());

		deleteMessagesInstant(message);
	}
}
