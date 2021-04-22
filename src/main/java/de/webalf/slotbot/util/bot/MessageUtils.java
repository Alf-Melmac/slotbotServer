package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.Role;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static de.webalf.slotbot.service.external.DiscordApiService.KNOWN_ROLE_NAMES;
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
	private static final int STANDARD_DELETION_TIME = 5; //In seconds

	/**
	 * Checks if the given message is not {@link Message#isFromGuild()}
	 *
	 * @param message to check
	 * @return true if the message in not from guild
	 */
	public static boolean isDm(@NonNull Message message) {
		return !message.isFromGuild();
	}

	/**
	 * Deletes the given messages with no delay
	 *
	 * @param messages to delete
	 */
	public static void deleteMessagesInstant(Message... messages) {
		deleteMessages(0, messages);
	}

	/**
	 * Deletes the given messages after the {@link #STANDARD_DELETION_TIME}
	 *
	 * @param messages to delete
	 */
	private static void deleteMessages(Message... messages) {
		deleteMessages(STANDARD_DELETION_TIME, messages);
	}

	/**
	 * Deletes the given messages after the given delay.
	 * <b>Doesn't delete messages that have not been sent on a server.</b>
	 *
	 * @param delay    in seconds
	 * @param messages to delete
	 */
	private static void deleteMessages(int delay, Message... messages) {
		Arrays.stream(messages).forEach(message -> {
			if (isDm(message)) {
				return;
			}
			message.delete().queueAfter(delay, TimeUnit.SECONDS);
		});
	}

	/**
	 * Deletes the latest message from the given channel
	 *
	 * @param channel in which the latest message should be deleted
	 */
	public static void deleteLatestMessageIfTypePinAdd(@NonNull MessageChannel channel) {
		final long latestMessageId = channel.getLatestMessageIdLong();
		channel.retrieveMessageById(latestMessageId).queue(message -> {
			if (MessageType.CHANNEL_PINNED_ADD == message.getType()) {
				deleteMessagesInstant(channel, latestMessageId);
			}
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
	 * Replies to the given message with the given reply.
	 * Deletes the user message and the reply with {@link #deleteMessages(Message...)}
	 *
	 * @param message to reply to
	 * @param reply   reply text
	 */
	public static void replyAndDelete(@NonNull Message message, @NotBlank String reply) {
		reply(message, reply, replyMessage -> deleteMessages(message, replyMessage));
	}

	/**
	 * Replies to the given message with the given reply.
	 * Deletes only the reply with {@link #deleteMessages(Message...)}
	 *
	 * @param message to reply to
	 * @param reply   reply text
	 */
	public static void replyAndDeleteOnlySend(@NonNull Message message, @NotBlank String reply) {
		reply(message, reply, MessageUtils::deleteMessages);
	}

	private static void reply(@NonNull Message message, @NotBlank String reply, Consumer<Message> success) {
		message.reply(reply).queue(success, fail -> log.warn("Failed to send reply", fail));
	}

	/**
	 * Sends the message author a direct message with the given text.
	 * Deletes the message afterwards with {@link #deleteMessagesInstant(Message...)}
	 *
	 * @param message     to reply in direct message to
	 * @param messageText message text
	 */
	public static void sendDmAndDeleteMessage(Message message, String messageText) {
		sendDm(message.getAuthor(), message, messageText, unused -> deleteMessagesInstant(message), true);
	}

	/**
	 * Sends the given message to the given recipient via {@link #sendDm(User, String, Consumer, Consumer)}
	 *
	 * @param message     that triggered this message sending
	 * @param recipientId the id of the user the message should be send to
	 * @param messageText text to send
	 * @param success     called after successful message send
	 */
	public static void sendDmToRecipient(@NonNull Message message, long recipientId, String messageText, Consumer<? super Message> success) {
		sendDm(message.getJDA().retrieveUserById(recipientId).complete(), message, messageText, success, false);
	}

	private static void sendDm(@NonNull User user, @NonNull Message message, @NotBlank String messageText, Consumer<? super Message> success, boolean callSuccessOnFailure) {
		final Consumer<? super Throwable> failure = fail -> {
			dmFailure(user, success, callSuccessOnFailure, fail);
			replyAndDeleteOnlySend(message, "Erlaube mir doch bitte dir eine private Nachricht zu senden :(");
		};

		sendDm(user, messageText, success, failure);
	}

	/**
	 * Sends the given message to the given recipient via {@link #sendDm(User, String)}
	 *
	 * @param event       that triggered this message sending
	 * @param recipientId the id of the user the message should be send to
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

	private static Consumer<Message> doNothing() {
		return unused -> {};
	}

	private static void dmFailure(User user, Consumer<? super Message> success, boolean callSuccessOnFailure, Throwable fail) {
		if (fail instanceof ErrorResponseException && ((ErrorResponseException) fail).getErrorResponse() == CANNOT_SEND_TO_USER) {
			log.warn("Couldn't send DM to {}. Probably the user prevents receiving messages from the bot.", user.getAsTag());
		} else {
			log.warn("Couldn't send DM to {}", user.getAsTag(), fail);
		}

		if (callSuccessOnFailure) {
			success.accept(null);
		}
	}

	/**
	 * Returns the known role {@link de.webalf.slotbot.service.external.DiscordApiService#KNOWN_ROLE_NAMES} names of the message author in the message guild
	 *
	 * @param message to analyze
	 * @return set of role names
	 */
	public static Set<Role> getKnownRoles(@NonNull Message message) {
		return message.getGuild().retrieveMember(message.getAuthor()).complete()
				.getRoles().stream()
				.map(net.dv8tion.jda.api.entities.Role::getName)
				.filter(KNOWN_ROLE_NAMES::contains)
				.map(Role::getByDiscordRole)
				.collect(Collectors.toUnmodifiableSet());
	}

	public static Consumer<Throwable> replyErrorMessage(Message message) {
		return failure -> replyAndDeleteOnlySend(message, failure.getMessage());
	}
}
