package de.webalf.slotbot.util;

import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.dtos.AbstractEventDto;
import de.webalf.slotbot.model.dtos.api.EventApiDto;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

import static de.webalf.slotbot.util.GuildUtils.GUILD_PLACEHOLDER;
import static de.webalf.slotbot.util.GuildUtils.Guild.findByDiscordGuild;
import static de.webalf.slotbot.util.bot.EmbedUtils.addField;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.*;
import static net.dv8tion.jda.api.utils.TimeFormat.DATE_TIME_SHORT;
import static net.dv8tion.jda.api.utils.TimeFormat.RELATIVE;

/**
 * @author Alf
 * @since 10.11.2020
 */
@UtilityClass
public final class EventUtils {
	/**
	 * Checks if the currently logged in can access the event with the given shareable and hidden state of the given ownerGuild
	 * <table><thead><tr><th style="text-align: left;"></th> <th style="text-align: left;">Visible + Not Shareable</th> <th style="text-align: left;">Hidden + Not Shareable</th> <th style="text-align: left;">Visible + Shareable</th> <th style="text-align: left;">Hidden + Shareable</th></tr></thead> <tbody><tr><td style="text-align: left;"><strong>Own Event</strong></td> <td style="text-align: left;">READ_PUBLIC</td> <td style="text-align: left;">READ</td> <td style="text-align: left;">READ_PUBLIC</td> <td style="text-align: left;">READ</td></tr> <tr><td style="text-align: left;"><strong>Foreign Event</strong></td> <td style="text-align: left;">X</td> <td style="text-align: left;">X</td> <td style="text-align: left;">READ_PUBLIC</td> <td style="text-align: left;">READ</td></tr></tbody></table>
	 *
	 * @param shareable  event shareable status
	 * @param hidden     event hidden status
	 * @param ownerGuild event owner guild
	 * @return true if access is allowed
	 */
	static boolean apiReadAccessAllowed(boolean shareable, boolean hidden, long ownerGuild) {
		if (shareable || isCurrentGuild(ownerGuild)) {
			return hasReadPermission(!hidden, ownerGuild);
		}
		return false;
	}

	/**
	 * Checks if read permission is given for the given event.
	 *
	 * @param eventDto event to check
	 * @throws ForbiddenException if read permission is not given
	 * @see #apiReadAccessAllowed(boolean, boolean, long)
	 */
	public static void assertApiReadAccess(@NonNull AbstractEventDto eventDto) throws ForbiddenException {
		if (!apiReadAccessAllowed(eventDto.getShareable(), eventDto.getHidden(), Long.parseLong(eventDto.getOwnerGuild()))) {
			throw new ForbiddenException("Not allowed to read here.");
		}
	}

	/**
	 * Checks if read permission is given for the given event.
	 *
	 * @param event event to check
	 * @throws ForbiddenException if read permission is not given
	 * @see #apiReadAccessAllowed(boolean, boolean, long)
	 */
	public static void assertApiReadAccess(@NonNull Event event) throws ForbiddenException {
		if (!apiReadAccessAllowed(event.isShareable(), event.isHidden(), event.getOwnerGuild())) {
			throw new ForbiddenException("Not allowed to read here.");
		}
	}

	/**
	 * Checks if write permission is given for the given event.
	 *
	 * @param ownerGuild event owner guild
	 * @return true if access is allowed
	 */
	private static boolean apiWriteAccessAllowed(long ownerGuild) {
		return hasWritePermission(ownerGuild);
	}

	/**
	 * Checks if write permission is given for the owner of the given event.
	 *
	 * @param event to check owner guild write permission for
	 * @throws ForbiddenException if write permission is not given
	 */
	public static void assertApiWriteAccess(AbstractEventDto event) throws ForbiddenException {
		final long ownerGuild = getOwnerGuild(event);
		if (!apiWriteAccessAllowed(ownerGuild)) {
			throw new ForbiddenException("Not allowed to write here.");
		}
	}

	/**
	 * Checks if write permission is given for the owner of the given event.
	 *
	 * @param event to check owner guild write permission for
	 * @throws ForbiddenException if write permission is not given
	 */
	public static void assertApiWriteAccess(Event event) throws ForbiddenException {
		final long ownerGuild = getOwnerGuild(event);
		if (!apiWriteAccessAllowed(ownerGuild)) {
			throw new ForbiddenException("Not allowed to write here.");
		}
	}

	public static long getOwnerGuild(@NonNull AbstractEventDto event) {
		return getOwnerGuild(LongUtils.parseLong(event.getOwnerGuild(), GUILD_PLACEHOLDER));
	}

	private static long getOwnerGuild(@NonNull Event event) {
		return getOwnerGuild(event.getOwnerGuild());
	}

	private static long getOwnerGuild(long ownerGuild) {
		final long currentOwnerGuild = GuildUtils.getCurrentOwnerGuild();
		return currentOwnerGuild != GUILD_PLACEHOLDER ? currentOwnerGuild : ownerGuild;
	}

	public static MessageEmbed buildDetailsEmbed(@NonNull EventApiDto event) {
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(Color.decode(event.getEventType().getColor()))
				.setTitle(event.getName(), fixUrl(event.getUrl(), event.getOwnerGuild()))
				.setDescription(event.getDescription())
				.setThumbnail(event.getPictureUrl())
				.setFooter(event.getEventType().getName() + " Mission von " + event.getCreator())
				.setTimestamp(Instant.now());

		if (event.getHidden()) {
			embedBuilder.setImage("https://cdn.discordapp.com/attachments/759147249325572097/789151354920632330/hidden_event.jpg");
		}

		addFields(embedBuilder, event);

		return embedBuilder.build();
	}

	/**
	 * Ensures that the url is an absolute uri
	 *
	 * @param url        to check
	 * @param ownerGuild owner of event
	 * @return usable url
	 */
	private static String fixUrl(String url, String ownerGuild) {
		//If the request was made from the discord the url is a relative URI, with absolute path
		//If an update is triggered by the website the url is an absolut URI
		//I wasn't able to find a fix for this other than this workaround :(
		if (!url.startsWith("http")) {
			return findByDiscordGuild(Long.parseLong(ownerGuild)).getBaseUrl() + url;
		}
		return url;
	}

	private static void addFields(@NonNull EmbedBuilder embedBuilder, @NonNull EventApiDto event) {
		addField("Zeitplan", buildScheduleField(event.getDateTimeZoned(), event.getMissionLength()), embedBuilder);
		addField("Missionstyp", event.getMissionType(), true, embedBuilder);
		addField("Reserve nimmt teil", buildReserveParticipatingField(event.getReserveParticipating()), true, embedBuilder);
		event.getDetails().forEach(field -> {
			String text = field.getText();
			if ("true".equals(text)) {
				text = "Ja";
			} else if ("false".equals(text)) {
				text = "Nein";
			}
			if (StringUtils.isNotEmpty(field.getLink())) {
				text = "[" + text + "](" + fixUrl(field.getLink(), event.getOwnerGuild()) + ")";
			}
			addField(field.getTitle(), text, true, embedBuilder);
		});
	}

	private static String buildScheduleField(ZonedDateTime eventDateTime, String missionLength) {
		final String dateTimeText = DATE_TIME_SHORT.format(eventDateTime) + " Uhr";
		return StringUtils.isNotEmpty(missionLength) ? dateTimeText + " und dauert " + missionLength : dateTimeText;
	}

	private static String buildReserveParticipatingField(Boolean reserveParticipating) {
		if (reserveParticipating == null) {
			return null;
		}
		return reserveParticipating ? "Ja" : "Nein";
	}

	/**
	 * Checks if the given slot number is already used in the given list of slots
	 *
	 * @param slots      existing slots
	 * @param slotNumber slot number to check
	 * @return true if the slot number is already used
	 */
	public static boolean slotNumberPresent(List<Slot> slots, int slotNumber) {
		return slots.stream().anyMatch(slot -> slot.getNumber() == slotNumber);
	}

	public static String buildNotificationMessage(@NonNull Event event) {
		return "**Erinnerung**: Das Event **" + event.getName() + "** geht " + RELATIVE.format(DateUtils.getDateTimeZoned(event.getDateTime())) + " los.";
	}

	public static String buildArchiveMessage(@NonNull Event event) {
		String message = "**__" + event.getName() + "__** " +
				DATE_TIME_SHORT.format(DateUtils.getDateTimeZoned(event.getDateTime())) +
				" " + event.getEventType().getName() + " ";
		if (StringUtils.isNotEmpty(event.getMissionType())) {
			message += event.getMissionType() + " ";
		}
		message += "von " + event.getCreator() + "; " + event.getShortInformation().getSlotCount() + " verfügbare Slots";
		return message;
	}
}
