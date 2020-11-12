package de.webalf.slotbot.util;

import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;

/**
 * @author Alf
 * @since 12.11.2020
 */
public class DiscordMarkdown {
	private static final String ONE_TIMES = "{1}";
	private static final String TWO_TIMES = "{2}";
	private static final String THREE_TIMES = "{3}";

	private static final String STAR_ITALICS = "\\*" + ONE_TIMES;
	private static final String UNDERSCORE_ITALICS = "_" + ONE_TIMES;
	private static final String BOLD = "\\*" + TWO_TIMES;
	private static final String BOLD_ITALICS = "\\*" + THREE_TIMES;
	private static final String UNDERLINE = "_" + TWO_TIMES;
	private static final String STRIKETHROUGH = "~" + TWO_TIMES;

	/**
	 * Imitates the discord markup by replacing the style symbols with html tags
	 *
	 * @return marked down string
	 * @see <a href="https://support.discord.com/hc/en-us/articles/210298617-Markdown-Text-101-Chat-Formatting-Bold-Italic-Underline-" target"_top">https://support.discord.com/hc/en-us/articles/210298617-Markdown-Text-101-Chat-Formatting-Bold-Italic-Underline-</a>
	 */
	public static String toHtml(@NotBlank String s) {
		String markdown;
		markdown = s.replaceAll("\n", "<br>");
		markdown = replace(markdown, "~~", STRIKETHROUGH, "s");
		markdown = replace(markdown, "__", UNDERLINE, "u");
		markdown = replaceMix(markdown, "***", BOLD_ITALICS, "strong", "em");
		markdown = replace(markdown, "**", BOLD, "strong");
		markdown = replace(markdown, "*", STAR_ITALICS, "em");
		markdown = replace(markdown, "_", UNDERSCORE_ITALICS, "em");

		return markdown;
	}

	private static String replace(String s, String symbol, String matcher, String tag) {
		while (StringUtils.countOccurrencesOf(s, symbol) >= 2) {
			s = replace(s, matcher, tag);
		}
		return s;
	}

	private static String replace(String s, String matcher, String tag) {
		return s.replaceFirst(matcher, "<" + tag + ">").replaceFirst(matcher, "</" + tag + ">");
	}

	private static String replaceMix(String s, String symbol, String matcher, String tag1, String tag2) {
		while (StringUtils.countOccurrencesOf(s, symbol) >= 2) {
			s = s.replaceFirst(matcher, "<" + tag1 + "><" + tag2 + ">")
					.replaceFirst(matcher, "</" + tag1 + "></" + tag2 + ">");
		}
		return s;
	}
}
