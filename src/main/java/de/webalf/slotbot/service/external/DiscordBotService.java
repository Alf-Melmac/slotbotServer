package de.webalf.slotbot.service.external;

import de.webalf.slotbot.model.external.discord.DiscordGuildMember;
import de.webalf.slotbot.model.external.discord.DiscordUser;
import de.webalf.slotbot.service.bot.BotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

/**
 * @author Alf
 * @since 27.01.2023
 */
@Service
@RequiredArgsConstructor
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class DiscordBotService {
	private final BotService botService;
	private final DiscordBotService self;

	/**
	 * Tries to build a {@link DiscordGuildMember}. If the {@link Guild#retrieveMemberById(long) member in the given guild}
	 * or {@link JDA#getGuildById(long) the guild} was not found it {@link JDA#retrieveUserById(long) retrieves the user}
	 * and builds a {@link DiscordGuildMember} containing only the {@link DiscordUser}.
	 *
	 * @param guildId to search member in
	 * @param userId  identifier
	 * @return the member or null if not resolvable
	 */
	@Cacheable("botGuildMember")
	public DiscordGuildMember getGuildMember(long userId, long guildId) {
		final JDA jda = botService.getJda();
		if (jda == null) {
			throw new IllegalStateException("JDA is not available");
		}
		final Guild guild = jda.getGuildById(guildId);

		if (guild != null) {
			try {
				final Member member = guild.retrieveMemberById(userId).complete();
				return DiscordGuildMember.fromJda(member);
			} catch (ErrorResponseException e) {
				if (e.getErrorResponse() != ErrorResponse.UNKNOWN_MEMBER) {
					log.warn("Failed to retrieve member {}", userId, e);
					return null;
				}
			}
		}

		try {
			final User user = jda.retrieveUserById(userId).complete();
			return DiscordGuildMember.builder().user(DiscordUser.fromJda(user)).build();
		} catch (ErrorResponseException _) {
			/*ignore*/
		}

		log.warn("Failed to retrieve user {}", userId);
		return null;
	}

	@Cacheable("botUser")
	public DiscordUser getUser(long userId) {
		final JDA jda = botService.getJda();
		if (jda == null) {
			throw new IllegalStateException("JDA is not available");
		}
		try {
			final User user = jda.retrieveUserById(userId).complete();
			return DiscordUser.fromJda(user);
		} catch (ErrorResponseException e) {
			log.warn("Failed to retrieve user {}", userId, e);
		}
		return null;
	}

	/**
	 * Returns the name of the user in the given guild.
	 * If no user can be found it returns {@link DiscordUser#UNKNOWN_USER_NAME}
	 *
	 * @param userId  user to get name for
	 * @param guildId guild to get name in
	 * @return name of the user in the guild
	 */
	public String getName(long userId, long guildId) {
		final DiscordGuildMember guildMember = self.getGuildMember(userId, guildId);
		if (guildMember != null) {
			return guildMember.getEffectiveName();
		}
		return DiscordUser.UNKNOWN_USER_NAME;
	}
}
