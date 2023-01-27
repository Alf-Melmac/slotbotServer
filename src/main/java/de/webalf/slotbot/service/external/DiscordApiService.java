package de.webalf.slotbot.service.external;

import de.webalf.slotbot.configuration.properties.DiscordProperties;
import de.webalf.slotbot.exception.IgnoreErrorResponseErrorHandler;
import de.webalf.slotbot.model.external.discord.DiscordGuildMember;
import de.webalf.slotbot.model.external.discord.DiscordUser;
import de.webalf.slotbot.util.LongUtils;
import de.webalf.slotbot.util.RestTemplatesUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Alf
 * @since 29.10.2020
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class DiscordApiService {
	private final DiscordProperties discordProperties;

	/**
	 * Returns the nickname for the given user(id) on the server or the username
	 *
	 * @param userId user to get name for
	 * @return nickname on server or username if not set
	 */
	@Cacheable("discordUsernames")
	public String getName(String userId) {
		return getUser(userId).getUsername();
	}

	@Cacheable("discordNicknames")
	public String getName(String userId, long guildId) {
		DiscordGuildMember guildMember = getGuildMemberWithUser(userId, guildId);
		return guildMember.getEffectiveName();
	}

	private static final String UNKNOWN_USER_NAME = "Unbekannter Nutzer";

	/**
	 * @see <a href="https://discord.com/developers/docs/resources/user#get-user" target"_top">https://discord.com/developers/docs/resources/user#get-user</a>
	 */
	@Cacheable(cacheNames = "discordUser")
	public DiscordUser getUser(String userId) {
		String url = "/users/" + userId;

		return buildWebClient().get().uri(url).retrieve().bodyToMono(DiscordUser.class)
				.onErrorResume(error -> {
					log.error("Failed to get user {}", userId, error);
					return Mono.just(DiscordUser.builder().username(UNKNOWN_USER_NAME).build());
				})
				.block();
	}

	public static boolean isUnknownUser(@NonNull DiscordUser user) {
		return UNKNOWN_USER_NAME.equals(user.getUsername());
	}

	private boolean wait = false;
	private long waitUntil;

	/**
	 * @see <a href="https://discord.com/developers/docs/resources/guild#get-guild-member" target="_top">https://discord.com/developers/docs/resources/guild#get-guild-member</a>
	 */
	private synchronized DiscordGuildMember getGuildMember(String userId, long guildId) {
		String url = "/guilds/" + guildId + "/members/" + userId;

		if (wait) {
			try {
				TimeUnit.SECONDS.sleep(Math.max(0, waitUntil - (System.currentTimeMillis() / 1000)));
			} catch (InterruptedException e) {
				log.error("Sleep was interrupted", e);
			}
			wait = false;
		}

		final ResponseEntity<DiscordGuildMember> response = RestTemplatesUtil
				.get("https://discord.com/api/v10" + url, discordProperties.getToken(), new IgnoreErrorResponseErrorHandler(), DiscordGuildMember.class);
		final HttpHeaders headers = response.getHeaders();
		List<String> remainingHeaders = headers.get("x-ratelimit-remaining");
		List<String> resetAfterHeaders = headers.get("x-ratelimit-reset-after");
		if (!CollectionUtils.isEmpty(remainingHeaders) && !CollectionUtils.isEmpty(resetAfterHeaders) && "0".equals(remainingHeaders.get(0))) {
			wait = true;
			waitUntil = (System.currentTimeMillis() / 1000) + LongUtils.parseCeilLongFromDoubleString(resetAfterHeaders.get(0));
		}
		final DiscordGuildMember guildMember = response.getBody();
		if (guildMember != null) guildMember.setGuild(guildId);
		return guildMember;
	}

	/**
	 * Returns the guild member. If not found it searches for the user itself and builds a {@link DiscordGuildMember}
	 *
	 * @param userId  user to search for
	 * @param guildId guild of the user
	 * @return {@link DiscordGuildMember} with the given user
	 */
	@Cacheable("guildMember")
	public DiscordGuildMember getGuildMemberWithUser(String userId, long guildId) {
		DiscordGuildMember member = getGuildMember(userId, guildId);
		if (member.getUser() == null) {
			log.warn("Fetching user of id " + userId);
			DiscordUser user = getUser(userId);
			member = DiscordGuildMember.builder().user(user).roles(Collections.emptySet()).build();
		}
		return member;
	}

	private WebClient buildWebClient() {
		return WebClient.builder()
				.baseUrl("https://discord.com/api/v10")
				.defaultHeader("Authorization", discordProperties.getToken())
				.build();
	}
}
