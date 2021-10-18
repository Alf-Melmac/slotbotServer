package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.assembler.api.EventApiAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.EventDiscordInformation;
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
import de.webalf.slotbot.util.GuildUtils.Guild;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static de.webalf.slotbot.util.GuildUtils.isDAA;
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
@SelectionMenuListener({"addEventToChannel", "addForeignEventToChannel"})
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
			final long guildId = message.getGuild().getIdLong();
			final Optional<EventDiscordInformation> discordInformation = event.getDiscordInformation(guildId);
			if (discordInformation.isPresent()) {
				replyAndDelete(message, "Das Event ist bereits " + discordInformation.get().getChannelAsMention() + " zugeordnet.");
				return;
			}

			final MessageChannel channel = message.getChannel();
			addEventAndPrint(event, channel, guildId);
			replyAndDelete(message, "Event " + event.getName() + " dem aktuellen Kanal hinzugefügt.");
		};
	}

	@Override
	public void execute(SlashCommandEvent slashCommandEvent) {
		log.trace("Slash command: addEventToChannel");

		if (existingEventInThisChannel(slashCommandEvent.getChannel())) {
			reply(slashCommandEvent, "Diesem Kanal ist bereits ein Event zugeordnet.");
			return;
		}

		final List<Event> events = eventBotService.findAllNotAssignedInFuture(slashCommandEvent.getGuild().getIdLong());
		final List<Event> foreignEvents = eventBotService.findAllForeignNotAssignedInFuture(slashCommandEvent.getGuild().getIdLong());
		final boolean eventsEmpty = events.isEmpty();
		final boolean foreignEventsEmpty = foreignEvents.isEmpty();
		if (eventsEmpty && foreignEventsEmpty) {
			reply(slashCommandEvent, "Kein nicht zugeordnetes Event in der Zukunft gefunden.");
			return;
		}

		final List<SelectionMenu> selectionMenus = new ArrayList<>(2);
		if (!eventsEmpty) {
			populateSelectionMenuList(events, selectionMenus, "Eigenes Event auswählen...", false);
		}
		if (!foreignEventsEmpty) {
			populateSelectionMenuList(foreignEvents, selectionMenus, "Fremdes Event auswählen...", true);
		}

		addSelectionMenu(slashCommandEvent, selectionMenus.toArray(new SelectionMenu[0]));
	}

	private void populateSelectionMenuList(List<Event> events, List<SelectionMenu> selectionMenus, String placeholder, boolean foreign) {
		final String[] menuIds = getClass().getAnnotation(SelectionMenuListener.class).value();
		final SelectionMenu.Builder selectionMenuBuilder = SelectionMenu.create(foreign ? menuIds[1] : menuIds[0])
				.setPlaceholder(placeholder);

		for (Event event : events) {
			final Guild guild = Guild.findByDiscordGuild(event.getOwnerGuild());
			if (guild == null) {
				throw new IllegalStateException("Found event (" + event.getId() + ") with non matching owner guild (" + event.getOwnerGuild() + ")");
			}
			final String name = (foreign ? "(" + guild.getId() + ") " : "") + event.getName();
			selectionMenuBuilder.addOption(buildSelectionLabel(name), Long.toString(event.getId()));
		}
		selectionMenus.add(selectionMenuBuilder.build());
	}

	@Override
	@Transactional
	public void process(SelectionMenuEvent selectionMenuEvent) {
		log.trace("Selection menu: addEventToChannel");

		final Event event = eventBotService.findById(Long.parseLong(selectionMenuEvent.getValues().get(0)));

		final long guildId = selectionMenuEvent.getGuild().getIdLong();
		final Optional<EventDiscordInformation> discordInformation = event.getDiscordInformation(guildId);
		if (discordInformation.isPresent()) {
			replyAndRemoveComponents(selectionMenuEvent, "Da war jemand schneller als du. Das Event ist bereits " + discordInformation.get().getChannelAsMention() + " zugeordnet.");
			return;
		}

		addEventAndPrint(event, selectionMenuEvent.getChannel(), guildId);
		replyAndRemoveComponents(selectionMenuEvent, "Event " + event.getName() + " dem aktuellen Kanal hinzugefügt.");
	}

	private boolean existingEventInThisChannel(@NonNull MessageChannel channel) {
		return eventBotService.findByChannel(channel.getIdLong()).isPresent();
	}

	private void addEventAndPrint(@NonNull Event event, @NonNull MessageChannel channel, long guildId) {
		final EventApiDto eventApiDto = EventApiAssembler.toDto(event);
		if (eventApiDto.getDiscordInformation() == null) {
			eventApiDto.setDiscordInformation(new HashSet<>());
		}
		//Set event channel
		final String guildIdString = Long.toString(guildId);
		eventApiDto.getDiscordInformation().add(EventDiscordInformationDto.builder().channel(channel.getId()).guild(guildIdString).build());

		channel.sendMessageEmbeds(EventUtils.buildDetailsEmbed(eventApiDto)) //Send event details
				.queue(infoMsgConsumer(channel, eventApiDto, guildIdString));
	}

	/**
	 * Called after the event details have been sent, to then send the first part of the slot list
	 */
	private Consumer<Message> infoMsgConsumer(@NonNull MessageChannel channel, @NonNull EventApiDto eventApiDto, String guildId) {
		return infoMsg -> {
			eventApiDto.getDiscordInformation(guildId).ifPresentOrElse(discordInformation -> discordInformation.setInfoMsg(infoMsg.getId()), () -> log.error("Failed to add infoMsg"));

			//Send Spacer
			final long ownerGuild = Long.parseLong(eventApiDto.getOwnerGuild());
			String spacer;
			if (isDAA(ownerGuild)) {
				spacer = "https://cdn.discordapp.com/attachments/759147249325572097/899736523275124827/Discord_Missionstrenner_DAA.png";
			} else {
				spacer = "https://cdn.discordapp.com/attachments/759147249325572097/798539020677808178/Discord_Missionstrenner.png";
			}
			sendMessage(channel, spacer);

			final List<String> slotListMessages = eventApiDto.getSlotList();
			if (slotListMessages.size() > 2) {
				throw BusinessRuntimeException.builder().title("Aktuell sind nur maximal zwei Slotlist-Nachrichten mit jeweils " + Message.MAX_CONTENT_LENGTH + " Zeichen möglich.").build();
			}
			//Send SlotList
			sendMessage(channel, ListUtils.shift(slotListMessages),
					slotListMsgConsumer(channel, eventApiDto, slotListMessages, guildId));
		};
	}

	/**
	 * Must be called after {@link #infoMsgConsumer(MessageChannel, EventApiDto, String)} to update the event with both message ids
	 */
	private Consumer<Message> slotListMsgConsumer(@NonNull MessageChannel channel, @NonNull EventApiDto eventApiDto, List<String> slotListMessages, String guildId) {
		return slotListMsg -> {
			eventApiDto.getDiscordInformation(guildId).ifPresent(discordInformation -> discordInformation.setSlotListMsgPartOne(slotListMsg.getId()));

			slotListMsg.pin().queue(unused -> deleteLatestMessageIfTypePinAdd(channel));

			sendMessage(channel, spacerCharIfEmpty(ListUtils.shift(slotListMessages)),
					slotListMsgLastConsumer(channel, eventApiDto, guildId));
		};
	}

	/**
	 * Must be called after {@link #slotListMsgConsumer(MessageChannel, EventApiDto, List, String)} to update the event with the last message id
	 */
	private Consumer<Message> slotListMsgLastConsumer(@NonNull MessageChannel channel, @NonNull EventApiDto eventApiDto, String guildId) {
		return slotListMsg -> {
			eventApiDto.getDiscordInformation(guildId).ifPresent(discordInformation -> discordInformation.setSlotListMsgPartTwo(slotListMsg.getId()));

			slotListMsg.pin().queue(unused -> deleteLatestMessageIfTypePinAdd(channel));

			eventBotService.updateEvent(eventApiDto);
		};
	}
}
