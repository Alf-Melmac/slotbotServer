package de.webalf.slotbot.model;

import de.webalf.slotbot.converter.persistence.PatternPersistenceConverter;
import de.webalf.slotbot.util.StringUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.regex.Pattern;

import static de.webalf.slotbot.util.MaxLength.TEXT;
import static de.webalf.slotbot.util.MaxLength.TEXT_DB;

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

	public static final long GUILD_PLACEHOLDER = -1L;

	public boolean is() {
		return urlPattern != null &&
				urlPattern.matcher(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString())
						.matches();
	}

	public String getBaseRedirectUrl() {
		return StringUtils.isNotEmpty(baseUrl) ? baseUrl : "https://armamachtbock.de";
	}

	public String getGroupIdentifier() {
		return StringUtils.isNotEmpty(groupIdentifier) ? groupIdentifier : Long.toString(id);
	}
}
