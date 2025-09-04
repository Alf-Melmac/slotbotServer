package de.webalf.slotbot.util;

import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.Slot;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

import static de.webalf.slotbot.util.StringUtils.trimAndNullify;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.hasReadPermission;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.isCurrentGuild;
import static net.dv8tion.jda.api.utils.TimeFormat.DATE_TIME_SHORT;

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
	 * Builds the event details url for the given event information
	 *
	 * @param eventId    event id to open details for
	 * @param ownerGuild guild owning the event
	 * @return uri to event details
	 */
	public static String buildUrl(long eventId, Guild ownerGuild) {
		return ownerGuild.getBaseRedirectUrl() + "/events/" + eventId;
	}

	/**
	 * Builds the event details url for the given event
	 *
	 * @param event event to open details for
	 * @return url to event details
	 * @see #buildUrl(long, Guild)
	 */
	public static String buildUrl(@NonNull Event event) {
		return buildUrl(event.getId(), event.getOwnerGuild());
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

	public static String buildArchiveMessage(@NonNull Event event) {
		final MessageSource messageSource = StaticContextAccessor.getBean(MessageSource.class);
		final Locale guildLocale = event.getOwnerGuildLocale();
		String message = maskedLink(event.getName(), buildUrl(event)) + " " + getDateTimeInDiscordFormat(event) + " " + event.getEventType().getName() + " ";
		if (StringUtils.isNotEmpty(event.getMissionType())) {
			message += event.getMissionType() + " ";
		}
		message += messageSource.getMessage("from", null, guildLocale) + " " + event.getCreator() + "; "
				+ event.getSlotCountWithoutReserve() + " " + messageSource.getMessage("event.archive.availableSlots", null, guildLocale);
		return message;
	}

	/**
	 * Workaround until JDA 6 is used: <a href="https://github.com/discord-jda/JDA/pull/2910">JDA#2910</a>
	 */
	private static String maskedLink(String text, String url) {
		return "[" + text + "](" + url + ")";
	}

	public static String getDateTimeInDiscordFormat(@NonNull Event event) {
		return DATE_TIME_SHORT.format(DateUtils.getDateTimeZoned(event.getDateTime()));
	}

	private static final Safelist SAFELIST = Safelist.none();

	static {
		SAFELIST.addTags("br", "s", "u", "strong", "em", "h1", "h2", "h3", "p", "ul", "ol", "li", "small");
	}

	public static String sanitize(String s) {
		final String out = trimAndNullify(s);
		if (out == null) {
			return null;
		}
		return Jsoup.clean(out, SAFELIST);
	}
}
