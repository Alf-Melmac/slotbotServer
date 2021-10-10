package de.webalf.slotbot.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;
import java.util.regex.Pattern;

import static de.webalf.slotbot.util.GuildUtils.Guild.*;

/**
 * @author Alf
 * @since 05.10.2021
 */
@UtilityClass
@Slf4j
public final class GuildUtils {
	public static final long GUILD_PLACEHOLDER = -1L;
	private static final Set<String> UNKNOWN_GROUPS = new HashSet<>();

	public static boolean isSlotbot() {
		return !isAMB() && !isDAA();
	}

	public static boolean isAMB() {
		return AMB.is();
	}

	public static boolean isDAA() {
		return DAA.is();
	}

	static String getCurrentGroupIdentifier() {
		final Guild currentGuild = findCurrentGuild();
		return currentGuild != null ? currentGuild.getId() : "Slotbot";
	}

	public static long getCurrentOwnerGuild() {
		final Guild currentGuild = findCurrentGuild();
		return currentGuild != null ? currentGuild.discordGuild : GUILD_PLACEHOLDER;
	}

	@Getter
	@AllArgsConstructor
	public enum Guild {
		AMB(Pattern.compile(".*(armamachtbock\\.de|arma-macht-bock\\.de).*"), "https://armamachtbock.de", "AMB", 701094826657054752L),
		DAA(Pattern.compile(".*(deutsche-arma-allianz\\.de|deutschearmaallianz\\.de).*"), "https://deutsche-arma-allianz.de", "DAA", 874650742089203792L),
		/*LOCALHOST(Pattern.compile(".*localhost.*"), "Test", AMB.getDiscordGuild())*/
		PUBLIC(Pattern.compile(".*todo.de.*"), "https://armamachtbock.de", "PUBLIC", GUILD_PLACEHOLDER);

		private final Pattern urlPattern;
		private final String baseUrl;
		private final String id;
		private final long discordGuild;

		private static final Map<Long, Guild> ID_GUILD_MAP;

		static {
			final Map<Long, Guild> idGuildMap = new HashMap<>();
			for (Guild guild : Guild.values()) {
				idGuildMap.put(guild.getDiscordGuild(), guild);
			}
			ID_GUILD_MAP = Collections.unmodifiableMap(idGuildMap);
		}

		static Guild findCurrentGuild() {
			final String currentUri = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
			for (Guild guild : Guild.values()) {
				if (guild.getUrlPattern().matcher(currentUri).matches()) {
					return guild;
				}
			}

			if (!UNKNOWN_GROUPS.contains(currentUri)) {
				log.warn("Searched for unknown group with uri '{}'", currentUri);
				UNKNOWN_GROUPS.add(currentUri);
			}
			return null;
		}

		public static Guild findByDiscordGuild(long discordGuild) {
			return ID_GUILD_MAP.get(discordGuild);
		}

		boolean is() {
			return getUrlPattern().matcher(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString()).matches();
		}
	}
}
