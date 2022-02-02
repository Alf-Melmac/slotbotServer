package de.webalf.slotbot.util;

import de.webalf.slotbot.controller.website.EventWebController;
import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.dtos.AbstractEventDto;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;

import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.hasReadPermission;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.isCurrentGuild;
import static net.dv8tion.jda.api.utils.TimeFormat.DATE_TIME_SHORT;
import static net.dv8tion.jda.api.utils.TimeFormat.RELATIVE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

	static boolean apiReadAccessAllowed(boolean shareable, boolean hidden, Guild ownerGuild) {
		return apiReadAccessAllowed(shareable, hidden, ownerGuild.getId());
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
	 * Builds the event details url for the given event
	 *
	 * @param eventId event id to open details for
	 * @return uri to event details
	 */
	public static String buildUrl(long eventId) {
		return linkTo(methodOn(EventWebController.class).getEventDetailsHtml(eventId)).toUri().toString();
	}

	/**
	 * Builds the event details url for the given event and validates with {@link #fixUrl(String, Guild)} that the url is an absolute uri
	 *
	 * @param event to build url for
	 * @return url to event details
	 */
	static String buildCorrectedUrl(@NonNull Event event) {
		return fixUrl(buildUrl(event.getId()), event.getOwnerGuild());
	}

	/**
	 * Ensures that the url is an absolute uri
	 *
	 * @param url        to check
	 * @param ownerGuild owner of event
	 * @return usable url
	 */
	static String fixUrl(String url, Guild ownerGuild) {
		//If the request was made from the discord the url is a relative URI, with absolute path
		//If an update is triggered by the website the url is an absolut URI
		//I wasn't able to find a fix for this other than this workaround :(
		if (!url.startsWith("http")) {
			return ownerGuild.getBaseRedirectUrl() + url;
		}
		return url;
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
		String message = "**__" + event.getName() + "__** " + getDateTimeInDiscordFormat(event) + " " + event.getEventType().getName() + " ";
		if (StringUtils.isNotEmpty(event.getMissionType())) {
			message += event.getMissionType() + " ";
		}
		message += "von " + event.getCreator() + "; " + event.getShortInformation().getSlotCount() + " verfügbare Slots";
		return message;
	}

	public static String getDateTimeInDiscordFormat(@NonNull Event event) {
		return DATE_TIME_SHORT.format(DateUtils.getDateTimeZoned(event.getDateTime()));
	}
}
