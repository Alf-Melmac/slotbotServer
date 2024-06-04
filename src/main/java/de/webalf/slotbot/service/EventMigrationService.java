package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.repository.EventRepository;
import de.webalf.slotbot.util.StringUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author Alf
 * @author TheConen
 * @since 31.05.2024
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EventMigrationService {
	private final EventRepository eventRepository;

	public void migrate() {
		final List<Event> events = eventRepository.findByDescriptionNotNull();
		log.info("Migrating {} events", events.size());

		events.forEach(event -> {
			final String oldDescription = event.getDescription();
			if (StringUtils.isEmpty(oldDescription) || oldDescription.startsWith("<p>")) {
				return;
			}

			log.info("Migrating {} ({})", event.getName(), event.getId());
			event.setDescription(toHtml(oldDescription));
		});
	}

	private static final Map<Pattern, HTML_TAG> MARKDOWN_TO_HTML_TAG_MAPPINGS = new LinkedHashMap<>();

	static {
		// The order matters (hence a LinkedHashMap).
		// Markdown formatting that consists of multiple characters (e.g. __underline__) needs to be processed
		// before formatting that consists of lesser characters (e.g. _italic_).
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?m)^(?<!\\\\)#{3}\\s(?<content>.+?)(?=\n|$)"), HTML_TAG.HTML_HEADING_3);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?m)^(?<!\\\\)#{2}\\s(?<content>.+?)(?=\n|$)"), HTML_TAG.HTML_HEADING_2);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?m)^(?<!\\\\)#\\s(?<content>.+?)(?=\n|$)"), HTML_TAG.HTML_HEADING_1);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("\n\n"), HTML_TAG.HTML_PARAGRAPH_REVERSED);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?<!</h[123]>)\n(?!$|<h[123]>)"), HTML_TAG.HTML_BREAK);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(^|\n)(?!<h[123]>)(?<content>.+?)(?=\n|$)"), HTML_TAG.HTML_PARAGRAPH);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?<!\\\\)\\*{2}(?<content>.+?)\\*{2}"), HTML_TAG.HTML_STRONG);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?<!\\\\)_{2}(?<content>.+?)_{2}"), HTML_TAG.HTML_UNDERLINE);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?<!\\\\)~{2}(?<content>.+?)~{2}"), HTML_TAG.HTML_STRIKETHROUGH);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?<!\\\\)\\*(?<content>.+?)\\*"), HTML_TAG.HTML_ITALIC);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?<!\\\\)_(?<content>.+?)_"), HTML_TAG.HTML_ITALIC);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("\\\\(?<content>[*_`~#\\\\])"), HTML_TAG.NOTHING);
		MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("\n"), HTML_TAG.NOTHING);
	}

	private static final Pattern PARAGRAPHED_HEADINGS = Pattern.compile("<p>(<h[123]>.+?</h[123]>)</p>");

	static String toHtml(String s) {
		String result = s;
		for (Map.Entry<Pattern, HTML_TAG> entry : MARKDOWN_TO_HTML_TAG_MAPPINGS.entrySet()) {
			final Pattern pattern = entry.getKey();
			final HTML_TAG htmlTag = entry.getValue();
			result = pattern.matcher(result).replaceAll(matchResult -> {
						Optional<String> content = Optional.empty();
						if (pattern.pattern().contains("content")) { // Skip regexes without "content" group
							content = Optional.ofNullable(matchResult.group("content"));
							// Funnily enough, if the escape character (backslash) is matched, we need to escape it again, otherwise replacing it will not work
							if (content.isPresent() && "\\".equals(content.get())) {
								content = Optional.of("\\\\");
							}
							if (pattern.pattern().equals("(^|\n)(?!<h[123]>)(?<content>.+?)(?=\n|$)")) {
								content = Optional.of(content.orElse("").replace("\\", "\\\\")); //Otherwise backslashes disappear?
							}
						}
						return htmlTag.enclose(content.orElse(""));
					}
			);
		}

		result = PARAGRAPHED_HEADINGS.matcher(result).replaceAll(matchResult -> "<p></p>" + matchResult.group(1));
		return result;
	}

	@Getter(AccessLevel.PRIVATE)
	@AllArgsConstructor
	private enum HTML_TAG {
		HTML_PARAGRAPH("<p>", "</p>"),
		HTML_PARAGRAPH_REVERSED("</p>", "<p>"),
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
