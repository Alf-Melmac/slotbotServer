package de.webalf.slotbot.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alf
 * @since 10.04.2023
 */
class DiscordMarkdownTest {
	private static final String HTML_BREAK = "<br>";
	private static final String HTML_STRIKETHROUGH = "s";
	private static final String HTML_UNDERLINE = "u";
	private static final String HTML_STRONG = "strong";
	private static final String HTML_ITALIC = "em";

	private static final String FORMATTED_BREAK = "\n" + HTML_BREAK + "\n";

	@ParameterizedTest(name = "{2}")
	@MethodSource
	void toHtml(String input, String expected, String name) {
		assertEquals(expected, DiscordMarkdown.toHtml(input));
	}

	private static Stream<Arguments> toHtml() {
		return Stream.of(
				Arguments.of("Hello World", "Hello World", "no markdown"),
				Arguments.of("*italics* or _italics_ **bold** ***bold italics*** __underline__ __*underline italics*__ __**underline bold**__ __***underline bold italics***__ ~~strikethrough~~",
						"<%1$s>italics</%1$s> or <%1$s>italics</%1$s> <%2$s>bold</%2$s> <%2$s><%1$s>bold italics</%1$s></%2$s> <%3$s>underline</%3$s> <%3$s><%1$s>underline italics</%1$s></%3$s> <%3$s><%2$s>underline bold</%2$s></%3$s> <%3$s><%2$s><%1$s>underline bold italics</%1$s></%2$s></%3$s> <%4$s>strikethrough</%4$s>".formatted(
								HTML_ITALIC, HTML_STRONG, HTML_UNDERLINE, HTML_STRIKETHROUGH),
						"text styles"),
				Arguments.of("""
						Hello
						World""", "Hello%sWorld".formatted(FORMATTED_BREAK), "line break"),
				Arguments.of("# Hello World", "<h1>Hello World</h1>", "Heading 1"),
				Arguments.of("## Hello World", "<h2>Hello World</h2>", "Heading 2"),
				Arguments.of("### Hello World", "<h3>Hello World</h3>", "Heading 3"),
				Arguments.of("""
						# Hello World
						Text""", "<h1>Hello World</h1>%sText".formatted(FORMATTED_BREAK), "heading with text"),
				Arguments.of("""
						Text
						# Hello World""", "Text%s<h1>Hello World</h1>".formatted(FORMATTED_BREAK), "text followed by heading"),
				Arguments.of("""
						# Heading 1
						h1
						## Heading 2
						h2

						### Heading 3
						h3""", "<h1>Heading 1</h1>%1$sh1%1$s<h2>Heading 2</h2>%1$sh2%1$s%2$s\n<h3>Heading 3</h3>%1$sh3".formatted(FORMATTED_BREAK, HTML_BREAK), "headings"),
				Arguments.of("Text # Hello World", "Text # Hello World", "inline heading"),
				Arguments.of("Evil <script>alert('Hello World');</script> <a href=\"https://example.com\">Link</a>",
						"Evil  Link", "filters other html tags")
		);
	}
}
