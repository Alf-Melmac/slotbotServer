package de.webalf.slotbot.util.bot;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.Interaction;
import org.springframework.util.CollectionUtils;

import java.util.function.Consumer;

/**
 * Util class to work with {@link Message}s
 *
 * @author Alf
 * @since 02.01.2021
 */
@UtilityClass
@Slf4j
public final class MessageUtils {
	/**
	 * Checks the last {@code 4} messages and deletes them from the given channel if {@link MessageType#CHANNEL_PINNED_ADD}
	 *
	 * @param channel in which the latest messages should be deleted
	 */
	public static void deletePinAddedMessages(@NonNull MessageChannel channel) {
		channel.getHistory().retrievePast(4).queue(messages -> {
			if (CollectionUtils.isEmpty(messages)) {
				return;
			}
			messages.forEach(message -> {
				if (MessageType.CHANNEL_PINNED_ADD == message.getType()) {
					channel.deleteMessageById(message.getIdLong()).queue();
				}
			});
		});
	}

	/**
	 * Sends the given text in the channel of the given message
	 * Shortcut for {@code sendMessage(interaction.getMessageChannel(), text)}
	 *
	 * @param interaction on which channel text should be sent
	 * @param message     to send
	 */
	public static void sendMessage(@NonNull Interaction interaction, @NotBlank String message) {
		sendMessage(interaction.getMessageChannel(), message);
	}

	/**
	 * Sends the given message in the given channel
	 *
	 * @param channel to send into
	 * @param message to send
	 */
	public static void sendMessage(@NonNull MessageChannel channel, @NotBlank String message) {
		sendMessage(channel, message, false);
	}

	/**
	 * Sends the given message in the given channel
	 *
	 * @param channel               to send into
	 * @param message               to send
	 * @param suppressNotifications suppress notifications for this message
	 */
	public static void sendMessage(@NonNull MessageChannel channel, @NotBlank String message, boolean suppressNotifications) {
		sendMessage(channel, message, suppressNotifications, doNothing());
	}

	/**
	 * Sends the given message in the given channel and queues the given success consumer
	 *
	 * @param channel                 to send into
	 * @param message                 to send
	 * @param suppressedNotifications suppress notifications for this message
	 * @param success                 message consumer
	 */
	public static void sendMessage(@NonNull MessageChannel channel, @NotBlank String message, boolean suppressedNotifications, Consumer<Message> success) {
		channel.sendMessage(message).setSuppressedNotifications(suppressedNotifications).queue(success);
	}

	static <T> Consumer<T> doNothing() {
		return unused -> {
		};
	}
}
