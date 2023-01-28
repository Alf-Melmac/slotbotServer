package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.service.GuildUsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Wrapper for {@link GuildUsersService} to be used by discord bot
 *
 * @author Alf
 * @since 28.01.2023
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GuildUsersBotService {
	private final GuildUsersService guildUsersService;

	public void add(long guildId, long userId) {
		guildUsersService.add(guildId, userId);
	}
}
