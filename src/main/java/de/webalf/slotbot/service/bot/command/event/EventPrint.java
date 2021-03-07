package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.assembler.api.EventApiAssembler;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.model.dtos.api.EventApiDto;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import de.webalf.slotbot.util.EventUtils;
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
				sendDmAndDeleteMessage(message, "Schau erstmal hier <https://discordapp.com/channels/" + message.getGuild().getId() + "/" + event.getChannel() + "/" + event.getInfoMsg() + "> oder hier <https://discordapp.com/channels/" + message.getGuild().getId() + "/" + event.getChannel() + "/" + event.getSlotListMsg() + ">. Mach meinetwegen ein !updateEvent aber doch nicht nochmal posten, dass will doch keiner.");
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
			eventApiDto.setInfoMsg(infoMsg.getId());

			//Send Spacer
			channel.sendMessage("https://cdn.discordapp.com/attachments/759147249325572097/798539020677808178/Discord_Missionstrenner.png").queue();

			channel.sendMessage(eventApiDto.getSlotList()) //Send SlotList
					.queue(slotListMsgConsumer(channel, eventApiDto));
		};
	}

	/**
	 * Must be called after {@link #infoMsgConsumer(MessageChannel, EventApiDto)} to update the event with both message ids
	 */
	private Consumer<Message> slotListMsgConsumer(@NonNull MessageChannel channel, @NonNull EventApiDto eventApiDto) {
		return slotListMsg -> {
			eventApiDto.setSlotListMsg(slotListMsg.getId());

			slotListMsg.pin().queue(unused -> deleteLatestMessage(channel));

			eventBotService.updateEvent(eventApiDto);
		};
	}


}
