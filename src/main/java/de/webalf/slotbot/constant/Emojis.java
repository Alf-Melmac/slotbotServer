package de.webalf.slotbot.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;

/**
 * @author Alf
 * @since 15.01.2021
 */
@Getter
@AllArgsConstructor
public enum Emojis {
	//Codepoint notation
	THUMBS_UP(Emoji.fromUnicode("U+1F44D")), //👍
	THUMBS_DOWN(Emoji.fromUnicode("U+1F44E")), //👎
	CHECKBOX(Emoji.fromUnicode("U+2611U+FE0F")), //☑️
	CROSS_MARK(Emoji.fromUnicode("U+274C")); //❌

	private final UnicodeEmoji emoji;

	/**
	 * Get emoji as formatted string to be used in messages
	 *
	 * @return Formatted string
	 */
	public String getFormatted() {
		return getEmoji().getFormatted();
	}
}
