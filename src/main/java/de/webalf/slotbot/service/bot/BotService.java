package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.service.bot.listener.GuildReadyListener;
import de.webalf.slotbot.service.bot.listener.InteractionListener;
import de.webalf.slotbot.service.bot.listener.MessageReceivedListener;
import de.webalf.slotbot.service.bot.listener.ReactionAddListener;
import de.webalf.slotbot.util.bot.CommandClassHelper;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import static de.webalf.slotbot.util.StringUtils.stripPrefixIfExists;
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
	private final CommandsService commandsService;
	private final MessageSource messageSource;

	@Getter
	private JDA jda;

	private static final String TOKEN_PREFIX = "Bot ";

	public void startUp() {
		String token = stripPrefixIfExists(discordProperties.getToken(), TOKEN_PREFIX);

		jda = JDABuilder
				.createLight(token)
				.enableIntents(GUILD_MEMBERS, GUILD_MESSAGES, DIRECT_MESSAGES, DIRECT_MESSAGE_REACTIONS, MESSAGE_CONTENT) //TODO Validate which intents are needed
				.addEventListeners(
						new MessageReceivedListener(discordProperties, commandClassHelper),
						new ReactionAddListener(reactionAddService),
						new GuildReadyListener(commandsService),
						new InteractionListener(commandClassHelper, messageSource))
				.disableIntents(GUILD_MODERATION, GUILD_EMOJIS_AND_STICKERS, GUILD_WEBHOOKS, GUILD_INVITES, GUILD_VOICE_STATES, GUILD_PRESENCES, GUILD_MESSAGE_REACTIONS, GUILD_MESSAGE_TYPING, DIRECT_MESSAGE_TYPING, SCHEDULED_EVENTS)
				.build();
	}

	@PreDestroy
	private void cleanUp() {
		jda.shutdownNow();
	}
}
