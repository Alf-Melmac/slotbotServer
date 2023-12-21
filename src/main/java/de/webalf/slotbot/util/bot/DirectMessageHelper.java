package de.webalf.slotbot.util.bot;

import de.webalf.slotbot.service.bot.BotService;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.requests.RestAction;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Function;

import static net.dv8tion.jda.api.requests.ErrorResponse.CANNOT_SEND_TO_USER;

/**
 * Util service to work with {@link Message}s in {@link PrivateChannel}s
 *
 * @author Alf
 * @since 02.01.2021
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DirectMessageHelper {
	private final BotService botService;

	/**
	 * Sends the given message to the given user. Logs an error if the message couldn't be sent.
	 *
	 * @param user        to send private message to
	 * @param messageText text to send
	 * @see #sendDm(User, String)
	 */
	public void sendDmToRecipient(@NonNull de.webalf.slotbot.model.User user, @NotBlank String messageText) {
		inPrivateChannel(user.getId(), channel -> channel.sendMessage(messageText), fail -> dmFailure(user.getId(), fail));
	}

	/**
	 * Sends the given message to the given user. Logs an error if the message couldn't be sent.
	 *
	 * @param user        to send private message to
	 * @param messageText text to send
	 */
	public static void sendDm(@NonNull User user, @NotBlank String messageText) {
		inPrivateChannel(user, privateChannel -> privateChannel.sendMessage(messageText), fail -> dmFailure(user.getIdLong(), fail));
	}

	/**
	 * Sends the given message to the given user
	 *
	 * @param user        to send private message to
	 * @param messageText text to send
	 * @param success     action to execute on success
	 * @param failure     action to execute on failure
	 * @param components  components to add to the message
	 */
	public static void sendDm(@NonNull User user, @NotBlank String messageText, Consumer<Message> success, Consumer<? super Throwable> failure, ItemComponent... components) {
		inPrivateChannel(user, privateChannel -> privateChannel.sendMessage(messageText).setActionRow(components), success, failure);
	}

	/**
	 * Edits the given message in the private channel of the given user. Replaces the message and removes all components.
	 *
	 * @param user        to edit private message of
	 * @param messageId   id of the message to edit
	 * @param messageText new text content
	 */
	public static void editDmAndRemoveComponents(@NonNull User user, long messageId, @NotBlank String messageText) {
		inPrivateChannel(user, channel -> channel
				.editMessageComponentsById(messageId)
				.flatMap(message -> message.editMessage(messageText)));
	}

	/**
	 * Removes the given message from the private channel of the given user
	 *
	 * @param userId    id of the recipient
	 * @param messageId id of the message to delete
	 */
	public void deleteDmOfRecipient(long userId, long messageId) {
		inPrivateChannel(userId, channel -> channel.deleteMessageById(messageId));
	}

	private <T> void inPrivateChannel(long userId, Function<PrivateChannel, RestAction<T>> action) {
		inPrivateChannel(userId, action, null);
	}

	/**
	 * Executes the given action in the private channel of the given user
	 *
	 * @param userId id of the recipient
	 * @param action action to execute in the private channel
	 */
	public <T> void inPrivateChannel(long userId, Function<PrivateChannel, RestAction<T>> action, Consumer<? super Throwable> failure) {
		botService.getJda().retrieveUserById(userId)
				.flatMap(User::openPrivateChannel)
				.flatMap(action)
				.queue(null, failure);
	}

	private static <T> void inPrivateChannel(@NonNull User user, Function<PrivateChannel, RestAction<T>> action) {
		inPrivateChannel(user, action, null);
	}

	private static <T> void inPrivateChannel(@NonNull User user, Function<PrivateChannel, RestAction<T>> action, Consumer<? super Throwable> failure) {
		inPrivateChannel(user, action, null, failure);
	}

	private static <T> void inPrivateChannel(@NonNull User user, Function<PrivateChannel, RestAction<T>> action, Consumer<T> success, Consumer<? super Throwable> failure) {
		user.openPrivateChannel()
				.flatMap(action)
				.queue(success, failure);
	}

	private static void dmFailure(long userId, Throwable fail) {
		if (fail instanceof ErrorResponseException ex && ex.getErrorResponse() == CANNOT_SEND_TO_USER) {
			log.warn("Couldn't send DM to {}. Probably the user prevents receiving messages from the bot.", userId);
		} else {
			log.warn("Couldn't send DM to {}", userId, fail);
		}
	}
}
