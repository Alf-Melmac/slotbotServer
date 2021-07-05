package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.assembler.api.EventApiAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.model.dtos.EventDiscordInformationDto;
import de.webalf.slotbot.model.dtos.api.EventApiDto;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import de.webalf.slotbot.util.EventUtils;
import de.webalf.slotbot.util.ListUtils;
import de.webalf.slotbot.util.StringUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Consumer;

import static de.webalf.slotbot.util.bot.MessageUtils.*;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.EVENT_MANAGE;

/**
 * @author Alf
 * @since 04.01.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Command(names = {"eventPrint", "showEvent", "printEvent"},
		description = "Gibt das Event des aktuellen Kanals aus.",
		authorization = EVENT_MANAGE)
public class EventPrint implements DiscordCommand {
	private final EventBotService eventBotService;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: eventPrint");

		final MessageChannel channel = message.getChannel();
		eventBotService.findByChannel(message, channel.getIdLong())
				.ifPresent(eventPrintConsumer(message, channel));
	}

	private Consumer<Event> eventPrintConsumer(Message message, @NonNull MessageChannel channel) {
		return event -> {
			if (event.isPrinted()) {
				sendDmAndDeleteMessage(message, "Schau erstmal hier <https://discordapp.com/channels/" + message.getGuild().getId() + "/" + event.getDiscordInformation().getChannel() + "/" + event.getDiscordInformation().getInfoMsg() + ">.");
				return;
			}

			final EventApiDto eventApiDto = EventApiAssembler.toDto(event);
			channel.sendMessage(EventUtils.buildDetailsEmbed(eventApiDto)) //Send event details
					.queue(infoMsgConsumer(channel, eventApiDto));

			deleteMessagesInstant(message);
		};
	}

	private Consumer<Message> infoMsgConsumer(@NonNull MessageChannel channel, @NonNull EventApiDto eventApiDto) {
		return infoMsg -> {
			eventApiDto.setDiscordInformation(EventDiscordInformationDto.builder().channel(channel.getId()).infoMsg(infoMsg.getId()).build());

			//Send Spacer
			channel.sendMessage("https://cdn.discordapp.com/attachments/759147249325572097/798539020677808178/Discord_Missionstrenner.png").queue();

			final List<String> slotListMessages = eventApiDto.getSlotList();
			if (slotListMessages.size() > 2) {
				throw BusinessRuntimeException.builder().title("Aktuell sind nur maximal zwei Slotlist-Nachrichten mit jeweils " + Message.MAX_CONTENT_LENGTH + " Zeichen möglich.").build();
			}
			channel.sendMessage(ListUtils.shift(slotListMessages)) //Send SlotList
					.queue(slotListMsgConsumer(channel, eventApiDto, slotListMessages));
		};
	}

	/**
	 * Must be called after {@link #infoMsgConsumer(MessageChannel, EventApiDto)} to update the event with both message ids
	 */
	private Consumer<Message> slotListMsgConsumer(@NonNull MessageChannel channel, @NonNull EventApiDto eventApiDto, List<String> slotListMessages) {
		return slotListMsg -> {
			eventApiDto.getDiscordInformation().setSlotListMsgPartOne(slotListMsg.getId());

			slotListMsg.pin().queue(unused -> deleteLatestMessageIfTypePinAdd(channel));

			channel.sendMessage(sendSpacerEmojiIfEmpty(ListUtils.shift(slotListMessages)))
					.queue(slotListMsgLastConsumer(channel, eventApiDto));
		};
	}

	public static String sendSpacerEmojiIfEmpty(String message) {
		return StringUtils.isEmpty(message) ? ":black_small_square:" : message;
	}

	/**
	 * Must be called after {@link #infoMsgConsumer(MessageChannel, EventApiDto)} to update the event with both message ids
	 */
	private Consumer<Message> slotListMsgLastConsumer(@NonNull MessageChannel channel, @NonNull EventApiDto eventApiDto) {
		return slotListMsg -> {
			eventApiDto.getDiscordInformation().setSlotListMsgPartTwo(slotListMsg.getId());

			slotListMsg.pin().queue(unused -> deleteLatestMessageIfTypePinAdd(channel));

			eventBotService.updateEvent(eventApiDto);
		};
	}

}
