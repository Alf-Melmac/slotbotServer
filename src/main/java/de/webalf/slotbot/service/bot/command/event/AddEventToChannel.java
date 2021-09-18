package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.assembler.api.EventApiAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.annotations.Command;
import de.webalf.slotbot.model.annotations.SelectionMenuListener;
import de.webalf.slotbot.model.annotations.SlashCommand;
import de.webalf.slotbot.model.dtos.EventDiscordInformationDto;
import de.webalf.slotbot.model.dtos.api.EventApiDto;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.command.DiscordCommand;
import de.webalf.slotbot.service.bot.command.DiscordSelectionMenu;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import de.webalf.slotbot.util.EventUtils;
import de.webalf.slotbot.util.ListUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Consumer;

import static de.webalf.slotbot.util.StringUtils.onlyNumbers;
import static de.webalf.slotbot.util.bot.EmbedUtils.spacerCharIfEmpty;
import static de.webalf.slotbot.util.bot.InteractionUtils.*;
import static de.webalf.slotbot.util.bot.MessageUtils.*;
import static de.webalf.slotbot.util.bot.SelectionMenuUtils.buildSelectionLabel;
import static de.webalf.slotbot.util.permissions.BotPermissionHelper.Authorization.EVENT_MANAGE;

/**
 * @author Alf
 * @since 04.01.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Command(names = {"addEventToChannel", "addChannel", "addEvent"},
		description = "Ordnet einem Event den aktuellen Kanal zu.",
		usage = "<Event-ID>",
		argCount = {1},
		authorization = EVENT_MANAGE)
@SlashCommand(name = "addEventToChannel",
		description = "Wähle ein Event aus und ordne es den aktuellen Kanal zu.",
		authorization = EVENT_MANAGE)
@SelectionMenuListener("addEventToChannel")
public class AddEventToChannel implements DiscordCommand, DiscordSlashCommand, DiscordSelectionMenu {
	private final EventBotService eventBotService;

	@Override
	public void execute(Message message, List<String> args) {
		log.trace("Command: addEventToChannel");

		final String eventId = args.get(0);
		if (!onlyNumbers(eventId)) {
			replyAndDelete(message, "Bitte übergebe an erster Stelle eine Event-ID.");
			return;
		}

		if (existingEventInThisChannel(message.getChannel())) {
			replyAndDelete(message, "Diesem Kanal ist bereits ein Event zugeordnet.");
			return;
		}

		eventBotService.findById(message, Long.parseLong(args.get(0)))
				.ifPresent(addEventConsumer(message));
	}

	private Consumer<Event> addEventConsumer(@NonNull Message message) {
		return event -> {
			if (event.isAssigned()) {
				replyAndDelete(message, "Das Event ist bereits " + event.getDiscordInformation().getChannelAsMention() + " zugeordnet.");
				return;
			}

			final MessageChannel channel = message.getChannel();
			addEventAndPrint(event, channel);
			replyAndDelete(message, "Event " + event.getName() + " dem aktuellen Kanal hinzugefügt.");
		};
	}

	@Override
	public void execute(SlashCommandEvent slashCommandEvent) {
		log.trace("Slash command: addEventToChannel");

		if (existingEventInThisChannel(slashCommandEvent.getChannel())) {
			reply(slashCommandEvent, "Diesem Kanal ist bereits ein Event zugeornet.");
			return;
		}

		final List<Event> events = eventBotService.findAllNotAssignedInFuture();
		if (events.isEmpty()) {
			reply(slashCommandEvent, "Kein nicht zugeordnetes Event in der Zukunft gefunden.");
			return;
		}

		final SelectionMenu.Builder selectionMenuBuilder = SelectionMenu.create(getClass().getAnnotation(SelectionMenuListener.class).value())
				.setPlaceholder("Event auswählen...");

		for (Event event : events) {
			selectionMenuBuilder.addOption(buildSelectionLabel(event.getName()), Long.toString(event.getId()));
		}

		addSelectionMenu(slashCommandEvent, selectionMenuBuilder.build());
	}

	@Override
	public void process(SelectionMenuEvent selectionMenuEvent) {
		log.trace("Selection menu: addEventToChannel");

		final Event event = eventBotService.findById(Long.parseLong(selectionMenuEvent.getValues().get(0)));

		if (event.isAssigned()) {
			replyAndRemoveComponents(selectionMenuEvent, "Da war jemand schneller als du. Das Event ist bereits " + event.getDiscordInformation().getChannelAsMention() + " zugeordnet.");
			return;
		}

		addEventAndPrint(event, selectionMenuEvent.getChannel());
		replyAndRemoveComponents(selectionMenuEvent, "Event " + event.getName() + " dem aktuellen Kanal hinzugefügt.");
	}

	private boolean existingEventInThisChannel(@NonNull MessageChannel channel) {
		return eventBotService.findByChannel(channel.getIdLong()).isPresent();
	}

	private void addEventAndPrint(@NonNull Event event, @NonNull MessageChannel channel) {
		final EventApiDto eventApiDto = EventApiAssembler.toDto(event);
		//Set event channel
		eventApiDto.setDiscordInformation(EventDiscordInformationDto.builder().channel(channel.getId()).build());

		channel.sendMessageEmbeds(EventUtils.buildDetailsEmbed(eventApiDto)) //Send event details
				.queue(infoMsgConsumer(channel, eventApiDto));
	}

	/**
	 * Called after the event details have been sent, to then send the first part of the slot list
	 */
	private Consumer<Message> infoMsgConsumer(@NonNull MessageChannel channel, @NonNull EventApiDto eventApiDto) {
		return infoMsg -> {
			eventApiDto.setDiscordInformation(EventDiscordInformationDto.builder().channel(channel.getId()).infoMsg(infoMsg.getId()).build());

			//Send Spacer
			sendMessage(channel, "https://cdn.discordapp.com/attachments/759147249325572097/798539020677808178/Discord_Missionstrenner.png");

			final List<String> slotListMessages = eventApiDto.getSlotList();
			if (slotListMessages.size() > 2) {
				throw BusinessRuntimeException.builder().title("Aktuell sind nur maximal zwei Slotlist-Nachrichten mit jeweils " + Message.MAX_CONTENT_LENGTH + " Zeichen möglich.").build();
			}
			//Send SlotList
			sendMessage(channel, ListUtils.shift(slotListMessages),
					slotListMsgConsumer(channel, eventApiDto, slotListMessages));
		};
	}

	/**
	 * Must be called after {@link #infoMsgConsumer(MessageChannel, EventApiDto)} to update the event with both message ids
	 */
	private Consumer<Message> slotListMsgConsumer(@NonNull MessageChannel channel, @NonNull EventApiDto eventApiDto, List<String> slotListMessages) {
		return slotListMsg -> {
			eventApiDto.getDiscordInformation().setSlotListMsgPartOne(slotListMsg.getId());

			slotListMsg.pin().queue(unused -> deleteLatestMessageIfTypePinAdd(channel));

			sendMessage(channel, spacerCharIfEmpty(ListUtils.shift(slotListMessages)),
					slotListMsgLastConsumer(channel, eventApiDto));
		};
	}

	/**
	 * Must be called after {@link #infoMsgConsumer(MessageChannel, EventApiDto)} to update the event with all message ids
	 */
	private Consumer<Message> slotListMsgLastConsumer(@NonNull MessageChannel channel, @NonNull EventApiDto eventApiDto) {
		return slotListMsg -> {
			eventApiDto.getDiscordInformation().setSlotListMsgPartTwo(slotListMsg.getId());

			slotListMsg.pin().queue(unused -> deleteLatestMessageIfTypePinAdd(channel));

			eventBotService.updateEvent(eventApiDto);
		};
	}
}
