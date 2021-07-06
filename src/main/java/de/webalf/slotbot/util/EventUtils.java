package de.webalf.slotbot.util;

import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.dtos.AbstractEventDto;
import de.webalf.slotbot.model.dtos.api.EventApiDto;
import de.webalf.slotbot.util.permissions.ApiPermissionHelper;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

import static de.webalf.slotbot.util.bot.EmbedUtils.addField;
import static net.dv8tion.jda.api.utils.TimeFormat.DATE_TIME_SHORT;

/**
 * @author Alf
 * @since 10.11.2020
 */
@UtilityClass
public final class EventUtils {
	/**
	 * Checks if the event, if hidden, is allowed to be read. Permission check is made by {@link ApiPermissionHelper#hasReadPermission()}.
	 *
	 * @param eventDto event to check
	 * @throws ForbiddenException if the event is hidden and read permission is not given
	 */
	public static void assertApiAccessAllowed(@NonNull AbstractEventDto eventDto) throws ForbiddenException {
		if (eventDto.isHidden() && !ApiPermissionHelper.hasReadPermission()) {
			throw new ForbiddenException("Access Denied");
		}
	}

	/**
	 * Works just like {@link #assertApiAccessAllowed(AbstractEventDto)} but expects a {@link Event} object
	 *
	 * @see #assertApiAccessAllowed(AbstractEventDto)
	 */
	public static void assertApiAccessAllowed(@NonNull Event event) throws ForbiddenException {
		if (event.isHidden() && !ApiPermissionHelper.hasReadPermission()) {
			throw new ForbiddenException("Access Denied");
		}
	}

	public static MessageEmbed buildDetailsEmbed(@NonNull EventApiDto event) {
		String thumbnail = event.getPictureUrl();
		if (StringUtils.isEmpty(thumbnail)) {
			thumbnail = "https://cdn.discordapp.com/attachments/759147249325572097/759147455483740191/AM-Blau-big-bananemitschokokuchen.jpg";
		}

		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(Color.decode(event.getEventType().getColor()))
				.setTitle(event.getName(), fixUrl(event.getUrl()))
				.setDescription(event.getDescription())
				.setThumbnail(thumbnail)
				.setFooter(event.getEventType().getName() + " Mission von " + event.getCreator())
				.setTimestamp(Instant.now());

		if (event.isHidden()) {
			embedBuilder.setImage("https://cdn.discordapp.com/attachments/759147249325572097/789151354920632330/hidden_event.jpg");
		}

		addFields(embedBuilder, event);

		return embedBuilder.build();
	}

	/**
	 * Ensures that the url is an absolute uri
	 *
	 * @param url to check
	 * @return usable url
	 */
	private String fixUrl(String url) {
		//If the request was made from the discord the url is a relative URI, with absolute path
		//If an update is triggered by the website the url is an absolut URI
		//I wasn't able to find a fix for this other than this workaround :(
		if (!url.startsWith("http")) {
			return "https://armamachtbock.de" + url;
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
				text = "[" + text + "](" + fixUrl(field.getLink()) + ")";
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
	public boolean slotNumberPresent(List<Slot> slots, int slotNumber) {
		return slots.stream().anyMatch(slot -> slot.getNumber() == slotNumber);
	}
}
