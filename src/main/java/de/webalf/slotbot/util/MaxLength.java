package de.webalf.slotbot.util;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Defines maxlength values for entities
 *
 * @author Alf
 * @since 01.08.2021
 */
@UtilityClass
public final class MaxLength {
	public static final int TEXT = 80;
	public static final int TEXT_DB = (int) (TEXT * 1.25);

	public static final int EMBEDDABLE_DESCRIPTION = MessageEmbed.DESCRIPTION_MAX_LENGTH;
	public static final int EMBEDDABLE_DESCRIPTION_DB = (int) (MessageEmbed.DESCRIPTION_MAX_LENGTH * 1.25);

	public static final int EMBEDDABLE_TITLE = MessageEmbed.TITLE_MAX_LENGTH;
	public static final int EMBEDDABLE_TITLE_DB = (int) (MessageEmbed.TITLE_MAX_LENGTH * 1.25);

	public static final int EMBEDDABLE_VALUE = MessageEmbed.VALUE_MAX_LENGTH;
	public static final int EMBEDDABLE_VALUE_DB = (int) (MessageEmbed.VALUE_MAX_LENGTH * 1.25);

	public static final int URL = 1666;
	public static final int URL_DB = 2083;

	public static final int COLOR_RGB = 7; //Expected format: #rrggbb
	public static final int COLOR_RGB_DB = COLOR_RGB;
}
