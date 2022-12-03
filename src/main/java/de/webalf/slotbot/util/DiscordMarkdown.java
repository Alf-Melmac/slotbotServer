package de.webalf.slotbot.util;

import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.util.StringUtils;

/**
 * Util class to work with discord formatting
 * @see <a href="https://support.discord.com/hc/en-us/articles/210298617-Markdown-Text-101-Chat-Formatting-Bold-Italic-Underline-" target"_top">https://support.discord.com/hc/en-us/articles/210298617-Markdown-Text-101-Chat-Formatting-Bold-Italic-Underline-</a>
 *
 * @author Alf
 * @since 12.11.2020
 */
@UtilityClass
public final class DiscordMarkdown {
	private static final String ONE_TIMES = "{1}";
	private static final String TWO_TIMES = "{2}";
	private static final String THREE_TIMES = "{3}";

	private static final String STAR_ITALICS = "\\*" + ONE_TIMES;
	private static final String UNDERSCORE_ITALICS = "_" + ONE_TIMES;
	private static final String BOLD = "\\*" + TWO_TIMES;
	private static final String BOLD_ITALICS = "\\*" + THREE_TIMES;
	private static final String UNDERLINE = "_" + TWO_TIMES;
	private static final String STRIKETHROUGH = "~" + TWO_TIMES;

	private static final String HTML_BREAK = "<br>";
	private static final String HTML_STRIKETHROUGH = "s";
	private static final String HTML_UNDERLINE = "u";
	private static final String HTML_STRONG = "strong";
	private static final String HTML_ITALIC = "em";

	private static final Safelist SAFELIST = Safelist.none();

	static {
		SAFELIST.addTags("br", HTML_STRIKETHROUGH, HTML_UNDERLINE, HTML_STRONG, HTML_ITALIC);
	}

	/**
	 * Imitates the discord markup by replacing the style symbols with html tags
	 *
	 * @return marked down string
	 */
	public static String toHtml(String s) {
		if (de.webalf.slotbot.util.StringUtils.isEmpty(s)) {
			return null;
		}

		String markdown;
		markdown = s.replace("\n", HTML_BREAK);
		markdown = replace(markdown, "~~", STRIKETHROUGH, HTML_STRIKETHROUGH);
		markdown = replace(markdown, "__", UNDERLINE, HTML_UNDERLINE);
		markdown = replaceMix(markdown, "***", BOLD_ITALICS, HTML_STRONG, HTML_ITALIC);
		markdown = replace(markdown, "**", BOLD, HTML_STRONG);
		markdown = replace(markdown, "*", STAR_ITALICS, HTML_ITALIC);
		markdown = replace(markdown, "_", UNDERSCORE_ITALICS, HTML_ITALIC);

		return Jsoup.clean(markdown, SAFELIST);
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
