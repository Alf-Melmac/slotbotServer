package de.webalf.slotbot.model.external.discord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Representation of the user returned by discord oauth2 flow
 *
 * @author Alf
 * @see <a href="https://discord.com/developers/docs/resources/user#user-object">User Object</a>
 * @since 27.01.2023
 */
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unused") //All fields are used by jackson. See CustomOAuth2UserService#getDiscordUser
public class DiscordOauthUser {
	private long id;
	private String username;
	private String globalName;
	private String avatar;
	private String locale;
}
