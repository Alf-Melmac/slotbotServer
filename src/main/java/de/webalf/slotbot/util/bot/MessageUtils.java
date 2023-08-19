package de.webalf.slotbot.util.bot;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.Interaction;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static net.dv8tion.jda.api.requests.ErrorResponse.CANNOT_SEND_TO_USER;

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
	 * Deletes the given messages with no delay.
	 * <p>
	 * <b>Only deletes messages that have been sent on a server.</b>
	 *
	 * @param messages to delete
	 */
	public static void deleteMessagesInstant(Message... messages) {
		Arrays.stream(messages).forEach(message -> {
			if (!message.isFromGuild()) {
				return;
			}
			message.delete().queueAfter(0, TimeUnit.SECONDS);
		});
	}

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
					deleteMessagesInstant(channel, message.getIdLong());
				}
			});
		});
	}

	/**
	 * Deletes the messages found by id in given channel
	 *
	 * @param channel    which includes the messages
	 * @param messageIds from the messages to delete
	 */
	public static void deleteMessagesInstant(@NonNull MessageChannel channel, long... messageIds) {
		for (long messageId : messageIds) {
			channel.deleteMessageById(messageId).queue();
		}
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
	 * @param channel to send into
	 * @param message to send
	 * @param success message consumer
	 */
	public static void sendMessage(@NonNull MessageChannel channel, @NotBlank String message, Consumer<Message> success) {
		sendMessage(channel, message, false, success);
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

	public static void sendDm(@NonNull User user, @NotBlank String messageText, Consumer<? super Message> success, boolean callSuccessOnFailure, @NonNull Consumer<String> replyConsumer) {
		final Consumer<? super Throwable> failure = fail -> {
			dmFailure(user, success, callSuccessOnFailure, fail);
			replyConsumer.accept("Erlaube mir doch bitte dir eine private Nachricht zu senden :(");
		};

		sendDm(user, messageText, success, failure);
	}

	/**
	 * Sends the given message to the given recipient via {@link #sendDm(User, String)}
	 *
	 * @param event       that triggered this message sending
	 * @param recipientId the id of the user the message should be sent to
	 * @param messageText text to send
	 */
	public static void sendDmToRecipient(@NonNull Event event, long recipientId, String messageText) {
		sendDm(event.getJDA().retrieveUserById(recipientId).complete(), messageText);
	}

	/**
	 * Sends the given message to the given user via {@link #sendDm(User, String, Consumer, Consumer)}
	 *
	 * @param user        to send private message to
	 * @param messageText text to send
	 */
	public static void sendDm(@NonNull User user, @NotBlank String messageText) {
		final Consumer<? super Throwable> failure = fail -> dmFailure(user, doNothing(), false, fail);

		sendDm(user, messageText, doNothing(), failure);
	}

	private static void sendDm(@NonNull User user, @NotBlank String messageText, Consumer<? super Message> success, Consumer<? super Throwable> failure) {
		user.openPrivateChannel().queue(
				privateChannel -> privateChannel.sendMessage(messageText).queue(success, failure),
				failure);
	}

	static Consumer<Message> doNothing() {
		return unused -> {
		};
	}

	private static void dmFailure(User user, Consumer<? super Message> success, boolean callSuccessOnFailure, Throwable fail) {
		if (fail instanceof ErrorResponseException ex && ex.getErrorResponse() == CANNOT_SEND_TO_USER) {
			log.warn("Couldn't send DM to @{}. Probably the user prevents receiving messages from the bot.", user.getName());
		} else {
			log.warn("Couldn't send DM to @{}", user.getName(), fail);
		}

		if (callSuccessOnFailure) {
			success.accept(null);
		}
	}
}
