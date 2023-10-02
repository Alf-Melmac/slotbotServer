package de.webalf.slotbot.model;

import de.webalf.slotbot.converter.persistence.PatternPersistenceConverter;
import de.webalf.slotbot.model.enums.Language;
import de.webalf.slotbot.util.StringUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Locale;
import java.util.regex.Pattern;

import static de.webalf.slotbot.util.ConstraintConstants.*;

/**
 * @author Alf
 * @since 04.01.2022
 */
@Entity
@Table(name = "discord_guild", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Guild extends AbstractDiscordIdEntity {
	@Column(name = "discord_guild_advanced", nullable = false, updatable = false)
	@Builder.Default
	private boolean advanced = false;

	@Column(name = "discord_guild_url_pattern")
	@Convert(converter = PatternPersistenceConverter.class)
	//Always set groupIdentifier when url pattern is defined!
	private Pattern urlPattern;

	@Column(name = "discord_guild_base_url", length = TEXT_DB)
	@Size(max = TEXT)
	private String baseUrl;

	@Column(name = "discord_guild_name", length = TEXT_DB)
	@Size(max = TEXT)
	private String groupIdentifier;

	@Column(name = "discord_guild_emoji")
	private Long emoji;

	@Column(name = "discord_guild_spacer_url", length = URL_DB)
	@Size(max = URL)
	private String spacerUrl;

	@Column(name = "discord_guild_language", nullable = false)
	@Enumerated(EnumType.STRING)
	@NonNull
	@Builder.Default
	private Language language = Language.DE;

	@Column(name = "discord_guild_archive_channel")
	private Long archiveChannel;

	public static final long GUILD_PLACEHOLDER = -1L;

	public String getBaseRedirectUrl() {
		return StringUtils.isNotEmpty(baseUrl) ? baseUrl : "https://slotbot.de";
	}

	public String getGroupIdentifier() {
		return StringUtils.isNotEmpty(groupIdentifier) ? groupIdentifier : Long.toString(id);
	}

	public String buildEmojiUrl() {
		return emoji != null ? "https://cdn.discordapp.com/emojis/" + emoji + ".webp" : null;
	}

	public String getSpacerUrl() {
		return StringUtils.isNotEmpty(spacerUrl) ? spacerUrl : "https://cdn.discordapp.com/attachments/759147249325572097/1084262121761677322/Discord_Missionstrenner_Slotbot.png";
	}

	public Locale getLocale() {
		return Locale.forLanguageTag(getLanguage().name());
	}
}
