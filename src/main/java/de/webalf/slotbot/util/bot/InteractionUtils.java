package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.constant.Emojis;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.Interaction;

import javax.validation.constraints.NotBlank;
import java.util.function.Consumer;

import static de.webalf.slotbot.util.bot.MessageUtils.doNothing;

/**
 * Util class to work with {@link Interaction}s
 *
 * @author Alf
 * @since 27.07.2021
 */
@UtilityClass
@Slf4j
public final class InteractionUtils {
	/**
	 * Acknowledge this interaction and defer the reply to a later time
	 *
	 * @param interaction to acknowledge
	 */
	public static void ephemeralDeferReply(@NonNull Interaction interaction) {
		interaction.deferReply(true).queue();
	}

	/**
	 * Replies to the given interaction with the given reply
	 *
	 * @param interaction to reply to
	 * @param reply       reply text
	 */
	public static void reply(@NonNull Interaction interaction, @NotBlank String reply) {
		reply(interaction, reply, doNothing());
	}

	private static void reply(@NonNull Interaction interaction, @NotBlank String reply, Consumer<Message> success) {
		interaction.getHook().sendMessage(reply).queue(success, fail -> log.warn("Failed to send interaction reply", fail));
	}

	/**
	 * Replies with a checkbox
	 *
	 * @param interaction finished interaction
	 */
	public static void finishedSlashCommandAction(@NonNull Interaction interaction) {
		reply(interaction, Emojis.CHECKBOX);
	}
}
