package de.webalf.slotbot.model;

import de.webalf.slotbot.converter.persistence.PatternPersistenceConverter;
import de.webalf.slotbot.model.enums.Language;
import de.webalf.slotbot.util.StringUtils;
import de.webalf.slotbot.util.permissions.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.utils.DiscordAssets;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static de.webalf.slotbot.util.ConstraintConstants.*;
import static net.dv8tion.jda.api.utils.ImageFormat.STATIC_WEBP;

/**
 * @author Alf
 * @since 04.01.2022
 */
@Entity
@Table(name = "guild", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"}),
		@UniqueConstraint(columnNames = {"discord_id"}, name = "discord_id_unique"),
		@UniqueConstraint(columnNames = {"guild_name"}, name = "group_name_unique")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Guild extends AbstractDiscordLinkable {
	@Column(name = "guild_advanced", nullable = false, updatable = false)
	@Builder.Default
	private boolean advanced = false;

	@Column(name = "guild_url_pattern")
	@Convert(converter = PatternPersistenceConverter.class)
	//Always set groupIdentifier when url pattern is defined!
	private Pattern urlPattern;

	@Column(name = "guild_base_url", length = TEXT_DB)
	@Size(max = TEXT)
	private String baseUrl;

	@Column(name = "guild_name", length = TEXT_DB)
	@Size(max = TEXT)
	private String groupIdentifier;

	@Column(name = "guild_discord_emoji")
	private Long emoji;

	@Column(name = "guild_spacer_url", length = URL_DB)
	@Size(max = URL)
	private String spacerUrl;

	@Column(name = "guild_language", nullable = false)
	@Enumerated(EnumType.STRING)
	@NonNull
	@Builder.Default
	private Language language = Language.DE;

	@Column(name = "guild_discord_archive_channel")
	private Long archiveChannel;

	@Column(name = "guild_discord_member_role")
	private Long memberRole;

	@Column(name = "guild_discord_event_manage_role")
	private Long eventManageRole;

	@Column(name = "guild_discord_admin_role")
	private Long adminRole;

	@OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BlogPost> blogPosts;

	public static final long GUILD_PLACEHOLDER = -1L;

	private static final String SLOTBOT_LOGO = "https://slotbot.de/backend/userContent/1/AM-name-slotbot-small.png";
	private static final String AMB_LOGO = "https://events.armamachtbock.de/backend/userContent/1/AM-Blau-small.jpg";
	private static final String DAA_LOGO = "https://events.deutsche-arma-allianz.de/backend/userContent/1/DAA_transparent.gif";
	private static final String TTT_LOGO = "https://events.tacticalteam.de/backend/userContent/1/slotbot_ttt-eventplatzhalter.png";

	//Special snowflakes
	private static final long AMB = 706254758721224707L;
	private static final long DAA = 874650742089203792L;
	private static final long TTT = 121399943393968128L;

	public String getLogo() {
		if (isAMB()) {
			return AMB_LOGO;
		} else if (is(DAA)) {
			return DAA_LOGO;
		} else if (is(TTT)) {
			return TTT_LOGO;
		}
		return SLOTBOT_LOGO;
	}

	public boolean isAMB() {
		return is(AMB);
	}

	private boolean is(long specialDiscordId) {
		final Long discordId = getDiscordId();
		return discordId != null && discordId == specialDiscordId;
	}

	public String getBaseRedirectUrl() {
		return StringUtils.isNotEmpty(baseUrl) ? baseUrl : "https://slotbot.de";
	}

	public String getGroupIdentifier() {
		return StringUtils.isNotEmpty(groupIdentifier) ? groupIdentifier : Long.toString(id);
	}

	public String buildEmojiUrl() {
		return emoji != null ? DiscordAssets.customEmoji(STATIC_WEBP, Long.toString(emoji)).getUrl() : null;
	}

	public String getSpacerUrl() {
		return StringUtils.isNotEmpty(spacerUrl) ? spacerUrl : "https://slotbot.de/backend/userContent/1/Discord_Missionstrenner_Slotbot.png";
	}

	public Locale getLocale() {
		return getLanguage().getRegionalLocale();
	}

	/**
	 * Returns the configured discord role for the given {@link Role}
	 *
	 * @param role to get discord role for
	 * @return discord role id or null if not configured
	 */
	public Long getDiscordRole(Role role) {
		if (role == Role.ADMINISTRATOR) {
			return getAdminRole();
		}
		if (role == Role.EVENT_MANAGE) {
			return getEventManageRole();
		}
		if (role == null) {
			return getMemberRole();
		}
		return null;
	}
}
