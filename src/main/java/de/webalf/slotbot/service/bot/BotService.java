package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.service.bot.listener.GuildReadyListener;
import de.webalf.slotbot.service.bot.listener.InteractionListener;
import de.webalf.slotbot.service.bot.listener.MessageReceivedListener;
import de.webalf.slotbot.service.bot.listener.ReactionAddListener;
import de.webalf.slotbot.util.bot.CommandClassHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.security.auth.login.LoginException;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;

/**
 * @author Alf
 * @since 01.01.2021
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BotService {
	private final DiscordProperties discordProperties;
	private final CommandClassHelper commandClassHelper;
	private final ReactionAddService reactionAddService;
	private final SlashCommandsService slashCommandsService;

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
					.enableIntents(GUILD_MEMBERS)
					.addEventListeners(
							new MessageReceivedListener(discordProperties, commandClassHelper),
							new ReactionAddListener(reactionAddService),
							new GuildReadyListener(slashCommandsService),
							new InteractionListener(commandClassHelper))
					.disableIntents(GUILD_BANS, GUILD_EMOJIS, GUILD_WEBHOOKS, GUILD_INVITES, GUILD_VOICE_STATES, GUILD_PRESENCES, GUILD_MESSAGE_REACTIONS, GUILD_MESSAGE_TYPING, DIRECT_MESSAGE_TYPING)
					.build();
		} catch (LoginException e) {
			log.error("Failed to start discord bot", e);
		}
	}

	@PreDestroy
	private void cleanUp() {
		jda.shutdownNow();
	}
}
