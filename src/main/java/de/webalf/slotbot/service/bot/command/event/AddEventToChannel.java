package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.assembler.api.EventApiAssembler;
import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.EventDiscordInformation;
import de.webalf.slotbot.model.annotations.bot.SlashCommand;
import de.webalf.slotbot.model.annotations.bot.StringSelectInteraction;
import de.webalf.slotbot.model.dtos.EventDiscordInformationDto;
import de.webalf.slotbot.model.dtos.api.EventApiDto;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.GuildBotService;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import de.webalf.slotbot.service.bot.command.DiscordStringSelect;
import de.webalf.slotbot.util.EventHelper;
import de.webalf.slotbot.util.ListUtils;
import de.webalf.slotbot.util.StaticContextAccessor;
import de.webalf.slotbot.util.bot.DiscordLocaleHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Consumer;

import static de.webalf.slotbot.service.GuildService.isDAA;
import static de.webalf.slotbot.util.bot.EmbedUtils.spacerCharIfEmpty;
import static de.webalf.slotbot.util.bot.InteractionUtils.*;
import static de.webalf.slotbot.util.bot.MessageUtils.deletePinAddedMessages;
import static de.webalf.slotbot.util.bot.MessageUtils.sendMessage;
import static de.webalf.slotbot.util.bot.StringSelectUtils.buildSelectLabel;

/**
 * @author Alf
 * @since 04.01.2021
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@SlashCommand(name = "bot.slash.event.addEventToChannel",
		description = "bot.slash.event.addEventToChannel.description",
		authorization = Permission.MANAGE_CHANNEL)
@StringSelectInteraction({"addEventToChannel", "addForeignEventToChannel"})
public class AddEventToChannel implements DiscordSlashCommand, DiscordStringSelect {
	private final EventBotService eventBotService;
	private final EventHelper eventHelper;
	private final GuildBotService guildBotService;

	@Override
	public void execute(@NonNull SlashCommandInteractionEvent event, @NonNull DiscordLocaleHelper locale) {
		log.trace("Slash command: addEventToChannel");

		if (existingEventInThisChannel(event.getChannel())) {
			reply(event, locale.t("bot.select.event.addEventToChannel.response.alreadyAnotherAssigned"));
			return;
		}

		//noinspection DataFlowIssue Guild only command
		final List<Event> events = eventBotService.findAllNotAssignedInFuture(event.getGuild().getIdLong());
		final List<Event> foreignEvents = eventBotService.findAllForeignNotAssignedInFuture(event.getGuild().getIdLong());
		final boolean eventsEmpty = events.isEmpty();
		final boolean foreignEventsEmpty = foreignEvents.isEmpty();
		if (eventsEmpty && foreignEventsEmpty) {
			reply(event, locale.t("bot.slash.event.addEventToChannel.response.noneFoundInFuture"));
			return;
		}

		final List<StringSelectMenu> selectMenus = new ArrayList<>(2);
		if (!eventsEmpty) {
			populateSelectMenuList(events, selectMenus, locale.t("bot.select.event.addEventToChannel.own"), false);
		}
		if (!foreignEventsEmpty) {
			populateSelectMenuList(foreignEvents, selectMenus, locale.t("bot.select.event.addEventToChannel.foreign"), true);
		}

		addSelectMenu(event, selectMenus.toArray(new StringSelectMenu[0]));
	}

	private void populateSelectMenuList(List<Event> events, @NonNull List<StringSelectMenu> selectMenus, String placeholder, boolean foreign) {
		final String[] menuIds = getClass().getAnnotation(StringSelectInteraction.class).value();
		final StringSelectMenu.Builder selectMenuBuilder = StringSelectMenu.create(foreign ? menuIds[1] : menuIds[0])
				.setPlaceholder(placeholder);

		for (Event event : events) {
			final String name = (foreign ? "(" + event.getOwnerGuild().getGroupIdentifier() + ") " : "") + event.getName();
			selectMenuBuilder.addOption(buildSelectLabel(name), Long.toString(event.getId()));
		}
		selectMenus.add(selectMenuBuilder.build());
	}

	@Override
	@Transactional
	public void process(@NonNull StringSelectInteractionEvent selectMenuEvent, @NonNull DiscordLocaleHelper locale) {
		log.trace("Selection menu: addEventToChannel");

		final Event event = eventBotService.findById(Long.parseLong(selectMenuEvent.getValues().get(0)));

		//noinspection DataFlowIssue Guild only command
		final long guildId = selectMenuEvent.getGuild().getIdLong();
		final Optional<EventDiscordInformation> discordInformation = event.getDiscordInformation(guildId);
		if (discordInformation.isPresent()) {
			replyAndRemoveComponents(selectMenuEvent, locale.t("bot.select.event.addEventToChannel.response.alreadyAssigned", discordInformation.get().getChannelAsMention()));
			return;
		}

		addEventAndPrint(event, selectMenuEvent.getChannel(), guildId);
		finishedVisibleInteraction(selectMenuEvent);
	}

	private boolean existingEventInThisChannel(MessageChannelUnion channel) {
		return eventBotService.findByChannel(channel.getIdLong()).isPresent();
	}

	private void addEventAndPrint(@NonNull Event event, MessageChannelUnion channel, long guildId) {
		final EventApiDto eventApiDto = EventApiAssembler.toDto(event);
		if (eventApiDto.getDiscordInformation() == null) {
			eventApiDto.setDiscordInformation(new HashSet<>());
		}
		//Set event channel
		final String guildIdString = Long.toString(guildId);
		eventApiDto.getDiscordInformation().add(EventDiscordInformationDto.builder().channel(channel.getId()).guild(guildIdString).build());

		final Locale guildLocale = guildBotService.getGuildLocale(guildId);
		channel.sendMessageEmbeds(eventHelper.buildDetailsEmbed(eventApiDto, guildLocale)) //Send event details
				.queue(infoMsgConsumer(channel, eventApiDto, guildIdString, guildLocale));
	}

	/**
	 * Called after the event details have been sent, to then send the first part of the slot list
	 */
	private Consumer<Message> infoMsgConsumer(MessageChannelUnion channel, @NonNull EventApiDto eventApiDto, String guildId, @NonNull Locale guildLocale) {
		return infoMsg -> {
			eventApiDto.getDiscordInformation(guildId).ifPresentOrElse(discordInformation -> discordInformation.setInfoMsg(infoMsg.getId()), () -> log.error("Failed to add infoMsg"));

			//Send Spacer
			final long ownerGuild = Long.parseLong(eventApiDto.getOwnerGuild());
			String spacer;
			if (isDAA(ownerGuild)) {
				spacer = "https://cdn.discordapp.com/attachments/759147249325572097/902303885228646420/Discord_Missionstrenner_DAA-transparent.png";
			} else {
				spacer = "https://cdn.discordapp.com/attachments/759147249325572097/798539020677808178/Discord_Missionstrenner.png";
			}
			sendMessage(channel, spacer, true);

			final List<String> slotListMessages = eventApiDto.getSlotList(Long.parseLong(guildId), StaticContextAccessor.getBean(MessageSource.class).getMessage("event.slotlist.title", null, guildLocale));
			if (slotListMessages.size() > 2) {
				throw BusinessRuntimeException.builder().title("Currently, only a maximum of two slotlist messages with " + Message.MAX_CONTENT_LENGTH + " characters each are possible.").build();
			}
			//Send SlotList
			sendMessage(channel, ListUtils.shift(slotListMessages), true,
					slotListMsgConsumer(channel, eventApiDto, slotListMessages, guildId));
		};
	}

	/**
	 * Must be called after {@link #infoMsgConsumer(MessageChannelUnion, EventApiDto, String, Locale)} to update the event with both message ids
	 */
	private Consumer<Message> slotListMsgConsumer(@NonNull MessageChannelUnion channel, @NonNull EventApiDto eventApiDto, List<String> slotListMessages, String guildId) {
		return slotListMsg -> {
			eventApiDto.getDiscordInformation(guildId).ifPresent(discordInformation -> discordInformation.setSlotListMsgPartOne(slotListMsg.getId()));

			slotListMsg.pin().queue();

			sendMessage(channel, spacerCharIfEmpty(ListUtils.shift(slotListMessages)), true,
					slotListMsgLastConsumer(channel, eventApiDto, guildId));
		};
	}

	/**
	 * Must be called after {@link #slotListMsgConsumer(MessageChannelUnion, EventApiDto, List, String)} to update the event with the last message id
	 */
	private Consumer<Message> slotListMsgLastConsumer(@NonNull MessageChannelUnion channel, @NonNull EventApiDto eventApiDto, String guildId) {
		return slotListMsg -> {
			eventApiDto.getDiscordInformation(guildId).ifPresent(discordInformation -> discordInformation.setSlotListMsgPartTwo(slotListMsg.getId()));

			slotListMsg.pin().queue(unused -> deletePinAddedMessages(channel));

			eventBotService.updateEvent(eventApiDto);
		};
	}
}
