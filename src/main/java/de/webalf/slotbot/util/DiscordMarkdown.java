package de.webalf.slotbot.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static de.webalf.slotbot.util.StringUtils.isEmpty;

/**
 * Util class to work with discord formatting
 *
 * @author Alf
 * @author TheConen
 * @see <a href="https://support.discord.com/hc/en-us/articles/210298617" target"_top">https://support.discord.com/hc/en-us/articles/210298617</a>
 * @since 12.11.2020
 */
@UtilityClass
@Slf4j
public final class DiscordMarkdown {
	private static final Map<Pattern, HTML_TAG> MARKDOWN_TO_HTML_TAG_MAPPINGS = new LinkedHashMap<>();
	private static final Safelist SAFELIST = Safelist.none();

	static {
		// The order matters (hence a LinkedHashMap).
		// Markdown formatting that consists of multiple characters (e.g. __underline__) needs to be processed
		// before formatting that consists of lesser characters (e.g. _italic_).
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?m)^(?<!\\\\)#{3}\\s(?<content>.+?)(\n|$)"), HTML_TAG.HTML_HEADING_3);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?m)^(?<!\\\\)#{2}\\s(?<content>.+?)(\n|$)"), HTML_TAG.HTML_HEADING_2);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?m)^(?<!\\\\)#\\s(?<content>.+?)(\n|$)"), HTML_TAG.HTML_HEADING_1);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("\n"), HTML_TAG.HTML_BREAK);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?<!\\\\)\\*{2}(?<content>.+?)\\*{2}"), HTML_TAG.HTML_STRONG);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?<!\\\\)_{2}(?<content>.+?)_{2}"), HTML_TAG.HTML_UNDERLINE);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?<!\\\\)~{2}(?<content>.+?)~{2}"), HTML_TAG.HTML_STRIKETHROUGH);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?<!\\\\)\\*(?<content>.+?)\\*"), HTML_TAG.HTML_ITALIC);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?<!\\\\)_(?<content>.+?)_"), HTML_TAG.HTML_ITALIC);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("\\\\(?<content>[*_`~#\\\\])"), HTML_TAG.NOTHING);

		SAFELIST.addTags("br", "s", "u", "strong", "em", "h1", "h2", "h3"); // Match these to the HTML_TAGs
	}

	/**
	 * Imitates the discord markup by replacing the style symbols with html tags
	 *
	 * @return marked down string
	 */
	public static String toHtml(String s) {
		if (isEmpty(s)) {
			return null;
		}

		String result = s;
		for (Map.Entry<Pattern, HTML_TAG> entry : MARKDOWN_TO_HTML_TAG_MAPPINGS.entrySet()) {
			final Pattern pattern = entry.getKey();
			final HTML_TAG htmlTag = entry.getValue();
			result = pattern.matcher(result).replaceAll(matchResult -> {
						Optional<String> content = Optional.empty();
						if (htmlTag != HTML_TAG.HTML_BREAK) { // Skip regexes without "content" group
							content = Optional.ofNullable(matchResult.group("content"));
							// Funnily enough, if the escape character (backslash) is matched, we need to escape it again, otherwise replacing it will not work
							if (content.isPresent() && "\\".equals(content.get())) {
								content = Optional.of("\\\\");
							}
						}
						return htmlTag.enclose(content.orElse(""));
					}
			);
		}

		return Jsoup.clean(result, SAFELIST);
	}

	@Getter(AccessLevel.PRIVATE)
	@AllArgsConstructor
	private enum HTML_TAG {
		HTML_BREAK("<br>", null),
		HTML_STRIKETHROUGH("<s>", "</s>"),
		HTML_UNDERLINE("<u>", "</u>"),
		HTML_STRONG("<strong>", "</strong>"),
		HTML_ITALIC("<em>", "</em>"),
		HTML_HEADING_1("<h1>", "</h1>"),
		HTML_HEADING_2("<h2>", "</h2>"),
		HTML_HEADING_3("<h3>", "</h3>"),
		NOTHING("", null);

		private final String openingTag;
		private final String closingTag;

		private String getClosingTag() {
			return closingTag != null ? closingTag : "";
		}

		public String enclose(String s) {
			return getOpeningTag() + s + getClosingTag();
		}
	}
}
