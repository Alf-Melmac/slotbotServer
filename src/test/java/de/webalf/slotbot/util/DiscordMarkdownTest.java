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

	@ParameterizedTest(name = "{2}")
	@MethodSource
	void toHtml(String input, String expected, String name) {
		assertEquals(expected, DiscordMarkdown.toHtml(input));
	}

	private static Stream<Arguments> toHtml() {
		return Stream.of(
				Arguments.of("Hello World", "Hello World", "no markdown"),
				Arguments.of("*italics* or _italics_ **bold** ***bold italics*** __underline__ __*underline italics*__ __**underline bold**__ __***underline bold italics***__ ~~strikethrough~~",
						String.format("<%1$s>italics</%1$s> or <%1$s>italics</%1$s> <%2$s>bold</%2$s> <%2$s><%1$s>bold italics</%1$s></%2$s> <%3$s>underline</%3$s> <%3$s><%1$s>underline italics</%1$s></%3$s> <%3$s><%2$s>underline bold</%2$s></%3$s> <%3$s><%2$s><%1$s>underline bold italics</%1$s></%2$s></%3$s> <%4$s>strikethrough</%4$s>",
								HTML_ITALIC, HTML_STRONG, HTML_UNDERLINE, HTML_STRIKETHROUGH),
						"text styles"),
				Arguments.of("""
						Hello
						World""", String.format("Hello\n%s\nWorld", HTML_BREAK), "line break"),
				Arguments.of("Evil <script>alert('Hello World');</script> <a href=\"https://example.com\">Link</a>",
						"Evil  Link", "filters other html tags")
		);
	}
}
