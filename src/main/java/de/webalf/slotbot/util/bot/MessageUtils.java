package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.util.PermissionHelper.Role;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static de.webalf.slotbot.service.external.DiscordApiService.KNOWN_ROLE_NAMES;

/**
 * Util class to work with {@link Message}s
 *
 * @author Alf
 * @since 02.01.2021
 */
@UtilityClass
@Slf4j
public class MessageUtils {
	private static final int STANDARD_DELETION_TIME = 3; //In seconds

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
		deleteMessagesWithDelay(0, messages);
	}

	/**
	 * Deletes the latest message from the given channel
	 *
	 * @param channel in which the latest message should be deleted
	 */
	public static void deleteLatestMessage(@NonNull MessageChannel channel) {
		deleteMessagesInstant(channel, channel.getLatestMessageIdLong());
	}

	/**
	 * Deletes the messages found by id in given channel
	 *
	 * @param channel which includes the messages
	 * @param messageIds from the messages to delete
	 */
	private static void deleteMessagesInstant(@NonNull MessageChannel channel, long... messageIds) {
		for (long messageId : messageIds) {
			channel.deleteMessageById(messageId).queue();
		}
	}

	/**
	 * Deletes the given messages after the {@link #STANDARD_DELETION_TIME}
	 *
	 * @param messages to delete
	 */
	private static void deleteMessages(Message... messages) {
		deleteMessagesWithDelay(STANDARD_DELETION_TIME, messages);
	}

	/**
	 * Deletes the given messages after the given delay.
	 * Doesn't delete messages that were not sent on a server.
	 *
	 * @param delay    in seconds
	 * @param messages to delete
	 */
	private static void deleteMessagesWithDelay(int delay, Message... messages) {
		Arrays.stream(messages).forEach(message -> {
			if (isDm(message)) {
				return;
			}
			message.delete().queueAfter(delay, TimeUnit.SECONDS);
		});
	}

	/**
	 * Replies to the given message with the given reply
	 * Deletes the user message and the reply with {@link #deleteMessages(Message...)}
	 *
	 * @param message to reply to
	 * @param reply   reply text
	 */
	public static void replyAndDelete(@NonNull Message message, @NotBlank String reply) {
		reply(message, reply, replyMessage -> deleteMessages(message, replyMessage));
	}

	/**
	 * Replies to the given message with the given reply
	 * Deletes only the reply with {@link #deleteMessages(Message...)}
	 *
	 * @param message to reply to
	 * @param reply   reply text
	 */
	private static void replyAndDeleteOnlySend(@NonNull Message message, @NotBlank String reply) {
		reply(message, reply, MessageUtils::deleteMessages);
	}

	private static void reply(@NonNull Message message, @NotBlank String reply) {
		reply(message, reply, null, null);
	}

	private static void reply(@NonNull Message message, @NotBlank String reply, Consumer<Message> success) {
		reply(message, reply, success, null);
	}

	private static void reply(@NonNull Message message, @NotBlank String reply, Consumer<? super Message> success, Consumer<? super Throwable> failure) {
		if (failure == null) {
			failure = fail -> log.warn("Failed to send reply", fail);
		}
		message.getChannel().sendMessage(message.getAuthor().getAsMention() + " " + reply).queue(success, failure);
	}

	private static void sendDm(@NonNull Message message, @NotBlank String messageText, Consumer<? super Message> success) {
		sendDm(message.getAuthor(), message, messageText, success, false);
	}

	private static void sendDm(@NonNull Message message, @NotBlank String messageText, Consumer<? super Message> success, boolean callSuccessOnFailure) {
		sendDm(message.getAuthor(), message, messageText, success, callSuccessOnFailure);
	}

	private static void sendDm(@NonNull User user, @NonNull Message message, @NotBlank String messageText, Consumer<? super Message> success) {
		sendDm(user, message, messageText, success, false);
	}

	public static void sendDmAndDeleteMessage(Message message, String messageText) {
		sendDm(message, messageText, unused -> deleteMessagesInstant(message), true);
	}

	private static void sendDm(@NonNull MessageReceivedEvent messageEvent, long recipientId, @NotBlank String messageText) {
		messageEvent.getJDA().retrieveUserById(recipientId).queue(
				user -> sendDm(user, messageEvent.getMessage(), messageText, message -> {})
		);
	}

	private static void sendDmAndDeleteMessage(@NonNull MessageReceivedEvent messageEvent, long recipientId, @NotBlank String messageText) {
		Message receivedMessage = messageEvent.getMessage();
		messageEvent.getJDA().retrieveUserById(recipientId).queue(
				user -> sendDm(user, receivedMessage, messageText, message -> deleteMessagesInstant(receivedMessage), true)
		);
	}

	private static void sendDm(@NonNull User user, @NonNull Message message, @NotBlank String messageText, Consumer<? super Message> success, boolean callSuccessOnFailure) {
		final Consumer<? super Throwable> failure = fail -> {
			dmFailure(user, success, callSuccessOnFailure, fail);
			replyAndDeleteOnlySend(message, "Erlaube mir doch bitte dir eine private Nachricht zu senden :(");
		};

		sendDm(user, messageText, success, failure);
	}

	static void sendDmWithoutMessage(@NonNull User user, @NotBlank String messageText) {
		final Consumer<? super Throwable> failure = fail -> dmFailure(user, unused -> {}, false, fail);

		sendDm(user, messageText, unused -> {}, failure);
	}

	private static void sendDm(@NonNull User user, @NotBlank String messageText, Consumer<? super Message> success, Consumer<? super Throwable> failure) {
		user.openPrivateChannel().queue(
				privateChannel -> privateChannel.sendMessage(messageText).queue(success, failure),
				failure);
	}

	private static void dmFailure(User user, Consumer<? super Message> success, boolean callSuccessOnFailure, Throwable fail) {
		log.warn("Couldn't send DM to {}", user.getAsTag(), fail);
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
	public static Set<Role> getKnownRoles(@NotNull Message message) {
		return message.getGuild().retrieveMember(message.getAuthor()).complete()
				.getRoles().stream()
				.map(net.dv8tion.jda.api.entities.Role::getName)
				.filter(KNOWN_ROLE_NAMES::contains)
				.map(Role::getByDiscordRole)
				.collect(Collectors.toUnmodifiableSet());
	}
}
