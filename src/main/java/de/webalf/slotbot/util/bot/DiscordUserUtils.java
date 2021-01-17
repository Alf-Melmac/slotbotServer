package de.webalf.slotbot.util.bot;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

/**
 * @author Alf
 * @since 17.01.2021
 */
@UtilityClass
public final class DiscordUserUtils {
	/**
	 * Returns the private channel for the given user by id
	 *
	 * @return the matching private channel or null if it doesn't exists
	 */
	public static PrivateChannel getPrivateChannel(@NonNull JDA jda, long userId) {
		final User user = jda.retrieveUserById(userId).complete();
		if (user != null && user.hasPrivateChannel()) {
			return user.openPrivateChannel().complete();
		}

		return null;
	}
}
