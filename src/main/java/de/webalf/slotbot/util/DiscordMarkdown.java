package de.webalf.slotbot.util;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Util class to work with discord formatting
 *
 * @author Alf
 * @see <a href="https://support.discord.com/hc/en-us/articles/210298617" target"_top">https://support.discord.com/hc/en-us/articles/210298617</a>
 * @since 12.11.2020
 */
@UtilityClass
public final class DiscordMarkdown {

    private static final Map<Pattern, HTML_TAG> MARKDOWN_TO_HTML_TAG_MAPPINGS = new LinkedHashMap<>();
    private static final Safelist SAFELIST = Safelist.none();

    static {
        // The order matters (hence a LinkedHashMap).
        // Markdown formatting that consists of multiple characters (eg. __underline__) needs to be processed
        // before formatting that consists of lesser characters (eg. _italic_).
        MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?m)^(?<!\\\\)#{3}\\s(?<content>.+?)(\n|$)"), HTML_TAG.HTML_HEADING_3);
        MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?m)^(?<!\\\\)#{2}\\s(?<content>.+?)(\n|$)"), HTML_TAG.HTML_HEADING_2);
        MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?m)^(?<!\\\\)#\\s(?<content>.+?)(\n|$)"), HTML_TAG.HTML_HEADING_1);
        MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("\n"), HTML_TAG.HTML_BREAK);
        MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?<!\\\\)\\*{3}(?<content>.+?)\\*{3}"), HTML_TAG.HTML_STRONG_ITALIC);
        MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?<!\\\\)\\*{2}(?<content>.+?)\\*{2}"), HTML_TAG.HTML_STRONG);
        MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?<!\\\\)_{2}(?<content>.+?)_{2}"), HTML_TAG.HTML_UNDERLINE);
        MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?<!\\\\)~{2}(?<content>.+?)~{2}"), HTML_TAG.HTML_STRIKETHROUGH);
        MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?<!\\\\)\\*(?<content>.+?)\\*"), HTML_TAG.HTML_ITALIC);
        MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("(?<!\\\\)_(?<content>.+?)_"), HTML_TAG.HTML_ITALIC);
        MARKDOWN_TO_HTML_TAG_MAPPINGS.put(Pattern.compile("\\\\(?<content>[*_`~\\\\])"), HTML_TAG.NOTHING);

        SAFELIST.addTags("br", "s", "u", "strong", "em", "h1", "h2", "h3");
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

        AtomicReference<String> result = new AtomicReference<>(s);

        MARKDOWN_TO_HTML_TAG_MAPPINGS.forEach((pattern, htmlTag) -> {
            Matcher matcher = pattern.matcher(result.get());
            result.set(matcher.replaceAll(matchResult -> {
                        Optional<String> content = Optional.empty();
                        try {
                            content = Optional.ofNullable(matchResult.group("content"));
                            // Funnily enough, if the escape character (backslash) is matched, we need to escape it again, otherwise replacing it will not work
                            if (content.isPresent() && "\\".equals(content.get())) {
                                content = Optional.of("\\\\");
                            }
                        } catch (IllegalArgumentException e) {
                            // Do nothing, this is fine, group "content" does not exist
                        }
                        return htmlTag.getOpeningTag() +
                                content.orElse("") +
                                htmlTag.getClosingTag()
                                        .orElse("");
                    }
            ));
        });

        return Jsoup.clean(result.get(), SAFELIST);
    }

    private enum HTML_TAG {
        HTML_BREAK("<br>", null),
        HTML_STRIKETHROUGH("<s>", "</s>"),
        HTML_UNDERLINE("<u>", "</u>"),
        HTML_STRONG("<strong>", "</strong>"),
        HTML_ITALIC("<em>", "</em>"),
        HTML_STRONG_ITALIC("<strong><em>", "</strong></em>"),
        HTML_HEADING_1("<h1>", "</h1>"),
        HTML_HEADING_2("<h2>", "</h2>"),
        HTML_HEADING_3("<h3>", "</h3>"),
        NOTHING("", null);

        @Getter
        private final String openingTag;
        private final String closingTag;

        HTML_TAG(String openingTag, @Nullable String closingTag) {
            this.openingTag = openingTag;
            this.closingTag = closingTag;
        }

        public Optional<String> getClosingTag() {
            return Optional.ofNullable(closingTag);
        }
    }
}
