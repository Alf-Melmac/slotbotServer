package de.webalf.slotbot.service.bot.command.event;

import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.EventDiscordInformation;
import de.webalf.slotbot.model.annotations.bot.SlashCommand;
import de.webalf.slotbot.model.annotations.bot.StringSelectInteraction;
import de.webalf.slotbot.model.dtos.EventDiscordInformationDto;
import de.webalf.slotbot.service.bot.EventBotService;
import de.webalf.slotbot.service.bot.GuildBotService;
import de.webalf.slotbot.service.bot.command.DiscordSlashCommand;
import de.webalf.slotbot.service.bot.command.DiscordStringSelect;
import de.webalf.slotbot.util.EventHelper;
import de.webalf.slotbot.util.ListUtils;
import de.webalf.slotbot.util.bot.DiscordLocaleHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Consumer;

import static de.webalf.slotbot.util.bot.ChannelUtils.botHasPermission;
import static de.webalf.slotbot.util.bot.EmbedUtils.spacerCharIfEmpty;
import static de.webalf.slotbot.util.bot.InteractionUtils.*;
import static de.webalf.slotbot.util.bot.MessageUtils.deletePinAddedMessages;
import static de.webalf.slotbot.util.bot.MessageUtils.sendMessage;
import static de.webalf.slotbot.util.bot.StringSelectUtils.buildSelectLabel;

/**
 * @author Alf
 * @since 04.01.2021
 */
@RequiredArgsConstructor
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

		final MessageChannelUnion eventChannel = event.getChannel();
		if (!eventChannel.canTalk() || !botHasPermission(eventChannel.asGuildMessageChannel(), Permission.MESSAGE_EMBED_LINKS)) {
			reply(event, locale.t("bot.interaction.response.cannotTalk"));
			return;
		}

		final long channelId = eventChannel.getIdLong();
		if (existingEventInThisChannel(channelId)) {
			reply(event, locale.t("bot.select.event.addEventToChannel.response.alreadyAnotherAssigned"));
			return;
		}
		//noinspection DataFlowIssue Guild only command
		final long guildId = event.getGuild().getIdLong();
		if (isArchiveChannel(guildId, channelId)) {
			reply(event, locale.t("bot.select.event.addEventToChannel.response.isArchive"));
			return;
		}

		final List<Event> events = eventBotService.findNotAssignedInFutureForSelect(guildId);
		final List<Event> foreignEvents = eventBotService.findForeignNotAssignedInFutureForSelect(guildId);
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

	private boolean existingEventInThisChannel(long channelId) {
		return eventBotService.findByChannel(channelId).isPresent();
	}

	private boolean isArchiveChannel(long guildId, long channelId) {
		return Objects.equals(guildBotService.getGuildArchiveChannel(guildId), channelId);
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

		final Event event = eventBotService.findById(Long.parseLong(selectMenuEvent.getValues().getFirst()));

		//noinspection DataFlowIssue Guild only command
		final long guildId = selectMenuEvent.getGuild().getIdLong();
		final Optional<EventDiscordInformation> discordInformation = event.getDiscordInformation(guildId);
		if (discordInformation.isPresent()) {
			replyAndRemoveComponents(selectMenuEvent, locale.t("bot.select.event.addEventToChannel.response.alreadyAssigned", discordInformation.get().getChannelAsMention()));
			return;
		}

		addEventAndPrint(event, selectMenuEvent.getChannel().asGuildMessageChannel(), guildId);
		finishedVisibleInteraction(selectMenuEvent);
	}

	private void addEventAndPrint(@NonNull Event event, GuildMessageChannel channel, long guildId) {
		//Set event channel and guild
		final EventDiscordInformationDto newInformation = EventDiscordInformationDto.builder()
				.channel(channel.getId())
				.guild(Long.toString(guildId))
				.build();

		final Locale guildLocale = guildBotService.getGuildLocale(guildId);
		channel.sendMessageEmbeds(eventHelper.buildDetailsEmbed(event, guildLocale)) //Send event details
				.queue(infoMsgConsumer(channel, event, newInformation, guildId, guildLocale));
	}

	/**
	 * Called after the event details have been sent, to then send the first part of the slot list
	 */
	private Consumer<Message> infoMsgConsumer(GuildMessageChannel channel, @NonNull Event event, @NonNull EventDiscordInformationDto discordInformation, long guildId, @NonNull Locale guildLocale) {
		return infoMsg -> {
			//Set info msg
			discordInformation.setInfoMsg(infoMsg.getId());

			//Send Spacer
			sendMessage(channel, event.getOwnerGuild().getSpacerUrl(), true);

			final List<String> slotListMessages = eventHelper.buildSlotList(event, guildId, guildLocale);
			if (slotListMessages.size() > 2) {
				throw BusinessRuntimeException.builder().title("Currently, only a maximum of two slotlist messages with " + Message.MAX_CONTENT_LENGTH + " characters each are possible.").build();
			}
			//Send SlotList
			sendMessage(channel, ListUtils.shift(slotListMessages), true,
					slotListMsgConsumer(channel, event.getId(), discordInformation, slotListMessages));
		};
	}

	/**
	 * Must be called after {@link #infoMsgConsumer(GuildMessageChannel, Event, EventDiscordInformationDto, long, Locale)} to update the event with both message ids
	 */
	private Consumer<Message> slotListMsgConsumer(GuildMessageChannel channel, long eventId, @NonNull EventDiscordInformationDto discordInformation, List<String> slotListMessages) {
		return slotListMsg -> {
			//Set slot list msg part one
			discordInformation.setSlotListMsgPartOne(slotListMsg.getId());

			boolean allowedToPin = true;
			if (botHasPermission(channel, Permission.MESSAGE_MANAGE)) {
				//Pin slotlist msg
				slotListMsg.pin().queue();
			} else {
				log.trace("Missing permission to pin messages in channel {} of guild {}", channel.getId(), channel.getGuild().getId());
				allowedToPin = false;
			}

			sendMessage(channel, spacerCharIfEmpty(ListUtils.shift(slotListMessages)), true,
					slotListMsgLastConsumer(channel, eventId, discordInformation, allowedToPin));
		};
	}

	/**
	 * Must be called after {@link #slotListMsgConsumer(GuildMessageChannel, long, EventDiscordInformationDto, List)} to update the event with the last message id
	 */
	private Consumer<Message> slotListMsgLastConsumer(GuildMessageChannel channel, long eventId, @NonNull EventDiscordInformationDto discordInformation, boolean allowedToPin) {
		return slotListMsg -> {
			//Set slot list msg part two
			discordInformation.setSlotListMsgPartTwo(slotListMsg.getId());

			if (allowedToPin) {
				//Pin second slotlist msg and remove pin information
				slotListMsg.pin().queue(unused -> deletePinAddedMessages(channel));
			}

			eventBotService.addDiscordInformation(eventId, discordInformation);
		};
	}
}
