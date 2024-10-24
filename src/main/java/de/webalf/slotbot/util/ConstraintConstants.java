package de.webalf.slotbot.util;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.MessageEmbed;

import static net.dv8tion.jda.api.requests.restaction.AuditableRestAction.MAX_REASON_LENGTH;

/**
 * Defines maxlength values for entities
 *
 * @author Alf
 * @since 01.08.2021
 */
@UtilityClass
public final class ConstraintConstants {
	public static final int TEXT = 80;
	public static final int TEXT_DB = (int) (TEXT * 1.25);

	public static final int EMBEDDABLE_TITLE = MessageEmbed.TITLE_MAX_LENGTH;
	public static final int EMBEDDABLE_TITLE_DB = (int) (EMBEDDABLE_TITLE * 1.25);

	public static final int EMBEDDABLE_VALUE = MessageEmbed.VALUE_MAX_LENGTH;
	public static final int EMBEDDABLE_VALUE_DB = (int) (EMBEDDABLE_VALUE * 1.25);

	public static final int URL = 1666;
	public static final int URL_DB = 2083;
	public static final String URL_PATTERN = "|\\s*(https?|attachment)://\\S+\\s*"; //See EmbedBuilder.URL_PATTERN

	public static final int HEX_COLOR = 7; //Expected format: #rrggbb
	public static final int HEX_COLOR_DB = HEX_COLOR;
	public static final String HEX_COLOR_PATTERN = "^#([a-f0-9]{6})$"; //Expected format: #rrggbb

	public static final int REASON = MAX_REASON_LENGTH;
	public static final int REASON_DB = (int) (REASON * 1.25);
}
