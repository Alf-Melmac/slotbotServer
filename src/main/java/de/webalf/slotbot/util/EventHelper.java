package de.webalf.slotbot.util;

import de.webalf.slotbot.feature.requirement.model.Requirement;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import de.webalf.slotbot.util.bot.MentionUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.*;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;

import static de.webalf.slotbot.util.bot.EmbedUtils.addField;
import static net.dv8tion.jda.api.utils.TimeFormat.DATE_TIME_SHORT;

/**
 * @author Alf
 * @since 05.01.2022
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventHelper {
	private final MessageSource messageSource;

	public MessageEmbed buildDetailsEmbed(@NonNull Event event, @NonNull Locale guildLocale) {
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(Color.decode(event.getEventType().getColor()))
				.setTitle(event.getName(), EventUtils.buildUrl(event))
				.setDescription(buildDescription(event.getDescription(), event::getId))
				.setThumbnail(event.getPictureUrl())
				.setFooter(messageSource.getMessage("bot.embed.event.footer", new String[]{event.getEventType().getName(), event.getCreator()}, guildLocale))
				.setTimestamp(Instant.now());

		if (event.isHidden()) {
			embedBuilder.setImage("https://slotbot.de/backend/userContent/1/hidden_event.jpg");
		}

		addFields(embedBuilder, event, guildLocale);

		return embedBuilder.build();
	}

	private String buildDescription(String description, LongSupplier eventId) {
		final String markdown = DiscordMarkdown.toMarkdown(description);
		if (markdown == null) {
			return null;
		}
		if (markdown.length() > MessageEmbed.DESCRIPTION_MAX_LENGTH) {
			log.warn("Event description too long {}", eventId.getAsLong());
			return markdown.substring(0, MessageEmbed.DESCRIPTION_MAX_LENGTH);
		}
		return markdown;
	}

	private void addFields(@NonNull EmbedBuilder embedBuilder, @NonNull Event event, @NonNull Locale guildLocale) {
		addField(messageSource.getMessage("bot.embed.event.details.schedule", null, guildLocale),
				buildScheduleField(DateUtils.getDateTimeZoned(event.getDateTime()), event.getMissionLength(), guildLocale),
				embedBuilder);
		addField(messageSource.getMessage("bot.embed.event.details.missionType", null, guildLocale),
				event.getMissionType(),
				true,
				embedBuilder);
		addField(messageSource.getMessage("bot.embed.event.details.reserveParticipating", null, guildLocale),
				buildReserveParticipatingField(event.getReserveParticipating(), guildLocale),
				true,
				embedBuilder);
		event.getDetails().forEach(field -> {
			String text = field.getText();
			if ("true".equals(text)) {
				text = messageSource.getMessage("yes", null, guildLocale);
			} else if ("false".equals(text)) {
				text = messageSource.getMessage("no", null, guildLocale);
			}
			if (StringUtils.isNotEmpty(field.getLink())) {
				text = "[" + text + "](" + field.getLink() + ")";
			}
			addField(field.getTitle(), text, true, embedBuilder);
		});
	}

	private String buildScheduleField(ZonedDateTime eventDateTime, String missionLength, @NonNull Locale guildLocale) {
		final String dateTimeText = DATE_TIME_SHORT.format(eventDateTime) + (guildLocale.getLanguage().equals("de") ? " Uhr" : "");
		return StringUtils.isNotEmpty(missionLength) ?
				messageSource.getMessage("bot.embed.event.details.schedule.text", new String[]{dateTimeText, missionLength}, guildLocale) : dateTimeText;
	}

	private String buildReserveParticipatingField(Boolean reserveParticipating, @NonNull Locale guildLocale) {
		if (reserveParticipating == null) {
			return null;
		}
		return reserveParticipating ? messageSource.getMessage("yes", null, guildLocale) : messageSource.getMessage("no", null, guildLocale);
	}

	/**
	 * Returns the slotlist for the given guild as content of a Discord message
	 * The list includes messages that do not exceed the discord message size limit
	 *
	 * @return slotlist
	 */
	public List<String> buildSlotList(@NonNull Event event, long guildId, @NonNull Locale guildLocale) {
		StringBuilder slotListText = new StringBuilder("__**").append(messageSource.getMessage("event.slotlist.title", null, guildLocale)).append("**__");
		addRequirements(slotListText, event.getRequirements());
		List<String> messages = new ArrayList<>();
		for (Squad squad : event.getSquadList()) {
			final StringBuilder squadText = toSlotList(squad, guildId);

			//Message can't fit new lines for new squad must start new message
			if (slotListText.length() + 2 > Message.MAX_CONTENT_LENGTH) {
				messages.add(slotListText.toString());
				slotListText = new StringBuilder();
			}

			//Existing message size + 2 new line + new line may not exceed maximum discord message size
			if (slotListText.length() + 2 + squadText.length() > Message.MAX_CONTENT_LENGTH) {
				slotListText.append("\n"); //New squad

				final List<String> splitSquadText = new ArrayList<>(Arrays.asList(squadText.toString().split("\\n"))); //Split squad text on every slot
				String nextSlotText = ListUtils.shift(splitSquadText);
				//noinspection ConstantConditions We know the squadText is bigger than the content length, nextSlotText will exist before reaching null values
				while (slotListText.length() + 1 + nextSlotText.length() < Message.MAX_CONTENT_LENGTH) { //Add slots until there is no more space in the message
					slotListText.append("\n").append(nextSlotText);
					nextSlotText = ListUtils.shift(splitSquadText);
				}

				messages.add(slotListText.toString());
				slotListText = new StringBuilder(nextSlotText).append("\n").append(String.join("\n", splitSquadText)); //Next slot list message must contain the rest of the squad
			} else {
				//First squad in new message doesn't need a leading new line
				if (!slotListText.isEmpty()) {
					slotListText.append("\n\n");
				}
				slotListText.append(squadText);
			}
		}
		messages.add(slotListText.toString());

		return messages;
	}

	/**
	 * Prepares the squad to be used in the slotlist
	 *
	 * @param guildId to prepare slotlist for
	 * @return squad in discord message format
	 */
	private StringBuilder toSlotList(@NonNull Squad squad, long guildId) {
		StringBuilder squadText = new StringBuilder("**").append(squad.getName()).append("**");
		final Guild reservedFor = squad.getReservedFor();
		if (reservedFor != null) {
			squadText.append(" [").append(reservedFor.getGroupIdentifier()).append("]");
		}

		addRequirements(squadText, squad.getRequirements());

		for (Slot slot : squad.getSlotList()) {
			squadText.append("\n").append(toSlotList(slot, guildId, reservedFor, squad.getSlotList()));
		}
		return squadText;
	}

	/**
	 * Prepares the slot to be used in the slotlist
	 *
	 * @param guildId          in which the slotlist will be printed
	 * @param squadReservedFor fallback if slot is not reserved
	 * @param squadSlots       all slots of the squad containing this slot
	 * @return slot in discord message format
	 */
	private StringBuilder toSlotList(@NonNull Slot slot, long guildId, Guild squadReservedFor, List<Slot> squadSlots) {
		StringBuilder slotText = new StringBuilder();

		boolean notReservedForOthers;
		final Guild reservedFor = slot.getReservedFor();
		if (reservedFor != null) { //Slot is reserved
			notReservedForOthers = guildId == reservedFor.getId();
		} else { //Use reservedFor of Squad
			notReservedForOthers = squadReservedFor == null || guildId == squadReservedFor.getId();
		}

		final boolean isEmpty = slot.getUser() == null;
		final boolean isFree = isEmpty && notReservedForOthers;
		if (isFree) {
			slotText.append("**");
		}
		slotText.append(slot.getNumber()).append(" ").append(slot.getName());
		if (isFree) {
			slotText.append("**");
		}

		final Guild reservedForDisplay = SlotUtils.getEffectiveReservedForDisplay(reservedFor, squadReservedFor, squadSlots);
		if (reservedForDisplay != null) {
			slotText.append(" [").append(reservedForDisplay.getGroupIdentifier()).append("]");
		}

		addRequirements(slotText, slot.getRequirements());

		slotText.append(":");

		final boolean isBlocked = !isEmpty && slot.getUser().isDefaultUser();
		if (!isEmpty && !isBlocked) {
			slotText.append(" ").append(MentionUtils.getUserAsMention(slot.getUser().getId()));
		} else if (isBlocked) {
			slotText.append(" *").append(slot.getReplacementTextOrDefault()).append("*");
		}
		return slotText;
	}

	private void addRequirements(StringBuilder text, Set<Requirement> requirements) {
		if (requirements.isEmpty()) {
			return;
		}
		text.append(" [")
				.append(requirements.stream().map(requirement -> {
					final String abbreviation = requirement.getAbbreviation();
					return StringUtils.isEmpty(abbreviation) ? requirement.getName() : abbreviation;
				}).collect(Collectors.joining(", ")))
				.append("]");
	}
}
