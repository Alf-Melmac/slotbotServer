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

	public static boolean isAMB(long guildId) {
		return AMB.getDiscordGuild() == guildId;
	}

	public static boolean isDAA() {
		return DAA.is();
	}

	public static boolean isDAA(long guildId) {
		return DAA.getDiscordGuild() == guildId;
	}

	static String getCurrentGroupIdentifier() {
		final Guild currentGuild = findCurrentGuild();
		return currentGuild != null ? currentGuild.getId() : "Slotbot";
	}

	public static long getCurrentOwnerGuild() {
		final Guild currentGuild = findCurrentGuild();
		return currentGuild != null ? currentGuild.discordGuild : GUILD_PLACEHOLDER;
	}

	private static final String SLOTBOT_LOGO = "https://cdn.discordapp.com/attachments/759147249325572097/899740543603589130/AM-name-slotbot-small.png";
	private static final String AMB_LOGO = "https://cdn.discordapp.com/attachments/759147249325572097/885282179796566046/AM-Blau-small.jpg";
	private static final String DAA_LOGO = "https://cdn.discordapp.com/attachments/759147249325572097/899747640634376272/DAA_transparent.gif";
	public static String getLogo(long guildId) {
		if (isAMB(guildId)) {
			return AMB_LOGO;
		} else if (isDAA(guildId)) {
			return DAA_LOGO;
		}
		return SLOTBOT_LOGO;
	}

	@Getter
	@AllArgsConstructor
	public enum Guild {
		AMB(Pattern.compile(".*(armamachtbock\\.de|arma-macht-bock\\.de).*"), "https://armamachtbock.de", "AMB", 706254758721224707L),
		DAA(Pattern.compile(".*(deutsche-arma-allianz\\.de|deutschearmaallianz\\.de).*"), "https://events.deutsche-arma-allianz.de", "DAA", 874650742089203792L),
		PUBLIC(Pattern.compile(".*localhost.*"), "https://armamachtbock.de", "PUBLIC", GUILD_PLACEHOLDER);

		private final Pattern urlPattern;
		private final String baseUrl;
		private final String id;
		private final long discordGuild;

		@Getter
		private static final Map<Long, Guild> ID_GUILD_MAP;
		private static final Map<String, Guild> STRING_ID_GUILD_MAP;

		static {
			final Map<Long, Guild> idGuildMap = new HashMap<>();
			final Map<String, Guild> stringIdGuildMap = new HashMap<>();
			for (Guild guild : Guild.values()) {
				idGuildMap.put(guild.getDiscordGuild(), guild);
				stringIdGuildMap.put(guild.getId(), guild);
			}
			ID_GUILD_MAP = Collections.unmodifiableMap(idGuildMap);
			STRING_ID_GUILD_MAP = Collections.unmodifiableMap(stringIdGuildMap);
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

		public static Guild findById(String id) {
			return STRING_ID_GUILD_MAP.get(id);
		}

		boolean is() {
			return getUrlPattern().matcher(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString()).matches();
		}
	}
}
