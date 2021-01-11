package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.util.bot.CommandEnumHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

/**
 * @author Alf
 * @since 01.01.2021
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BotService {
	private final DiscordProperties discordProperties;
	private final CommandEnumHelper commandEnumHelper;

	@Getter
	private JDA jda;

	private static final String TOKEN_PREFIX = "Bot ";

	public void startUp() {
		String token = discordProperties.getToken();
		if (token.startsWith(TOKEN_PREFIX)) {
			token = token.substring(TOKEN_PREFIX.length());
		}

		try {
			jda = JDABuilder
					//TODO: maybe default (validate caching)
					.createLight(token)
					.enableIntents(GatewayIntent.GUILD_MEMBERS)
					.addEventListeners(new MessageListener(discordProperties, commandEnumHelper))
					.build();
		} catch (LoginException e) {
			log.error("Failed to start discord bot", e);
		}
	}
}
