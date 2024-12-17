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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
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
@RequiredArgsConstructor
@Slf4j
public class DiscordApiService {
	private final DiscordProperties discordProperties;

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

		DiscordGuildMember guildMember;
		try {
			final ResponseEntity<DiscordGuildMember> response = RestTemplatesUtil
					.get("https://discord.com/api/v10" + url, discordProperties.getToken(), new IgnoreErrorResponseErrorHandler(), DiscordGuildMember.class);
			final HttpHeaders headers = response.getHeaders();
			final List<String> remainingHeaders = headers.get("x-ratelimit-remaining");
			final List<String> resetAfterHeaders = headers.get("x-ratelimit-reset-after");
			if (!CollectionUtils.isEmpty(remainingHeaders) && !CollectionUtils.isEmpty(resetAfterHeaders) && "0".equals(remainingHeaders.getFirst())) {
				wait = true;
				waitUntil = (System.currentTimeMillis() / 1000) + LongUtils.parseCeilLongFromDoubleString(resetAfterHeaders.getFirst());
			}
			guildMember = response.getBody();
		} catch (HttpClientErrorException e) { //Catch member not found exception
			guildMember = null;
		}
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
		if (member == null) {
			final DiscordUser user = getUser(userId);
			member = DiscordGuildMember.builder().user(user).roles(Collections.emptySet()).build();
		}
		return member;
	}

	private WebClient buildWebClient() {
		return WebClient.builder()
				.baseUrl("https://discord.com/api/v10")
				.defaultHeader(HttpHeaders.AUTHORIZATION, discordProperties.getToken())
				.build();
	}
}
