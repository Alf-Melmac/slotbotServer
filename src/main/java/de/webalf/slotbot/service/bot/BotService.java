package de.webalf.slotbot.service.bot;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.service.EventDiscordInformationService;
import de.webalf.slotbot.service.bot.listener.GuildContentListener;
import de.webalf.slotbot.service.bot.listener.GuildEventListener;
import de.webalf.slotbot.service.bot.listener.InteractionListener;
import de.webalf.slotbot.util.bot.CommandClassHelper;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import static de.webalf.slotbot.util.StringUtils.stripPrefixIfExists;
import static net.dv8tion.jda.api.requests.GatewayIntent.SCHEDULED_EVENTS;
import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.*;

/**
 * @author Alf
 * @since 01.01.2021
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BotService {
	private final DiscordProperties discordProperties;
	private final CommandsService commandsService;
	private final EventDiscordInformationService eventDiscordInformationService;
	private final GuildUsersBotService guildUsersBotService;
	private final CommandClassHelper commandClassHelper;
	private final MessageSource messageSource;
	private final GuildBotService guildBotService;

	@Getter
	private JDA jda;

	private static final String TOKEN_PREFIX = "Bot ";

	public void startUp() {
		String token = stripPrefixIfExists(discordProperties.getToken(), TOKEN_PREFIX);

		jda = JDABuilder
				.create(token, GUILD_MEMBERS, GUILD_MESSAGES)
				.addEventListeners(
						new GuildEventListener(commandsService, eventDiscordInformationService, guildUsersBotService),
						new InteractionListener(commandClassHelper, messageSource),
						new GuildContentListener(eventDiscordInformationService, guildBotService, messageSource))
				.disableIntents(GUILD_MODERATION, GUILD_EMOJIS_AND_STICKERS, GUILD_WEBHOOKS, GUILD_INVITES, GUILD_VOICE_STATES, GUILD_PRESENCES, GUILD_MESSAGE_REACTIONS, GUILD_MESSAGE_TYPING, DIRECT_MESSAGES, DIRECT_MESSAGE_REACTIONS, DIRECT_MESSAGE_TYPING, MESSAGE_CONTENT, SCHEDULED_EVENTS, AUTO_MODERATION_CONFIGURATION, AUTO_MODERATION_EXECUTION)
				.disableCache(ACTIVITY, VOICE_STATE, EMOJI, STICKER, CLIENT_STATUS, ONLINE_STATUS, CacheFlag.SCHEDULED_EVENTS)
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.setChunkingFilter(ChunkingFilter.ALL)
				.setLargeThreshold(250)
				.build();
	}

	@PreDestroy
	private void cleanUp() {
		if (jda != null) {
			jda.shutdownNow();
		}
	}
}
