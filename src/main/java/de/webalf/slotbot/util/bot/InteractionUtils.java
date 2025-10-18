package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.constant.Emojis;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IDeferrableCallback;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;

import java.util.Arrays;

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
	public static void ephemeralDeferReply(@NonNull IReplyCallback interaction) {
		interaction.deferReply(true).queue();
	}

	/**
	 * Acknowledge this interaction and defer edit to a later time
	 *
	 * @param interaction to acknowledge
	 */
	public static void deferEdit(@NonNull IMessageEditCallback interaction) {
		interaction.deferEdit().queue();
	}

	/**
	 * Replies to the given interaction with the given reply
	 *
	 * @param interaction to reply to
	 * @param reply       reply text
	 */
	public static void reply(@NonNull IDeferrableCallback interaction, @NotBlank String reply) {
		interaction.getHook().sendMessage(reply).queue(null, fail -> log.warn("Failed to send interaction reply", fail));
	}

	/**
	 * Sends a message without text content, but the given {@link StringSelectMenu}
	 *
	 * @param interaction to add select menu to
	 * @param selectMenu  to add
	 * @see #addSelectMenu(IDeferrableCallback, String, StringSelectMenu)
	 */
	public static void addSelectMenu(@NonNull IDeferrableCallback interaction, StringSelectMenu... selectMenu) {
		interaction.getHook()
				.sendMessageComponents(Arrays.stream(selectMenu).map(ActionRow::of).toList())
				.useComponentsV2()
				.queue();
	}

	/**
	 * Sends a message with the given {@link StringSelectMenu}
	 *
	 * @param interaction to add select menu to
	 * @param reply       message to send
	 * @param selectMenu  to add
	 * @see #addSelectMenu(IDeferrableCallback, StringSelectMenu...)
	 */
	public static void addSelectMenu(@NonNull IDeferrableCallback interaction, @NotBlank String reply, StringSelectMenu selectMenu) {
		interaction.getHook()
				.sendMessage(reply)
				.addComponents(ActionRow.of(selectMenu))
				.queue();
	}

	/**
	 * Replies to the given interaction with the given reply and removes all action rows
	 *
	 * @param interaction to reply to
	 * @param reply       reply text
	 */
	public static void replyAndRemoveComponents(@NonNull ComponentInteraction interaction, @NotBlank String reply) {
		interaction.getHook().editOriginal(reply).setComponents().queue();
	}

	/**
	 * Replies with a checkbox
	 *
	 * @param interaction finished interaction
	 */
	public static void finishedInteraction(@NonNull IDeferrableCallback interaction) {
		reply(interaction, Emojis.CHECKBOX.getFormatted());
	}

	/**
	 * Removes the original interaction response
	 *
	 * @param interaction finished interaction
	 */
	public static void finishedVisibleInteraction(@NonNull IDeferrableCallback interaction) {
		interaction.getHook().deleteOriginal().queue();
	}

	/**
	 * Replies with an error message
	 *
	 * @param interaction failed interaction
	 * @param message     additional information about the error
	 */
	public static void failedInteraction(@NonNull IDeferrableCallback interaction, String message) {
		reply(interaction, Emojis.CROSS_MARK.getFormatted() + " " + message);
	}
}
