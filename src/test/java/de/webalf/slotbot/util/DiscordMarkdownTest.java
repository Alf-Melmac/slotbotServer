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
	@ParameterizedTest(name = "{2}")
	@MethodSource
	void toMarkdown(String input, String expected, String name) {
		assertEquals(expected, DiscordMarkdown.toMarkdown(input));
	}

	private static Stream<Arguments> toMarkdown() {
		return Stream.of(
				Arguments.of("Hello World", "Hello World", "no markdown"),
				Arguments.of("<p><em>italics</em> <strong>bold</strong> <strong><em>bold italics</em></strong> <u>underline</u> <em><u>underline italics</u></em> <strong><u>underline bold</u></strong> <strong><em><u>underline bold italics</u></em></strong> <s>strikethrough</s></p>",
						"*italics* **bold** ***bold italics*** __underline__ *__underline italics__* **__underline bold__** ***__underline bold italics__*** ~~strikethrough~~",
						"text styles"),
				Arguments.of("<p><em>Italic</em></p>", "*Italic*", "italic"),
				Arguments.of("<p><strong>Bold</strong></p>", "**Bold**", "bold"),
				Arguments.of("<p><strong><em>bold italics</strong></em></p>", "***bold italics***", "bold italics"),
				Arguments.of("<p><u>underline</u></p>", "__underline__", "underline"),
				Arguments.of("<p><u><em>underline italics</em></u></p>", "__*underline italics*__", "underline italics"),
				Arguments.of("<p><u><strong>underline bold</strong></u></p>", "__**underline bold**__", "underline bold"),
				Arguments.of("<p><u><strong><em>underline bold italics</strong></em></u></p>", "__***underline bold italics***__", "underline bold italics"),
				Arguments.of("<p><s>strikethrough</s></p>", "~~strikethrough~~", "strikethrough"),
				Arguments.of("<p>Hello</p><p>World</p>", """
						Hello
						World""", "line break"),
				Arguments.of("<h1>Heading 1</h1>", "# Heading 1", "Heading 1"),
				Arguments.of("<h2>Heading 2</h2>", "## Heading 2", "Heading 2"),
				Arguments.of("<h3>Heading 3</h3>", "### Heading 3", "Heading 3"),
				Arguments.of("<h1>Hello World</h1><p>Text</p>", """
						# Hello World
						Text""", "heading with text"),
				Arguments.of("<p>Text</p><h1>Hello World</h1>", """
						Text
						# Hello World""", "text followed by heading"),
				Arguments.of("<h1>Heading 1</h1><p>h1</p><h2>Heading 2</h2><p>h2</p><p></p><h3>Heading 3</h3><p>h3</p>", """
						# Heading 1
						h1
						## Heading 2
						h2

						### Heading 3
						h3""", "headings"),
				Arguments.of("<p>Text # Hello World</p>", "Text # Hello World", "inline heading"),
				Arguments.of("<p># Escaped</p><h1># One heading</h1><p>## First escaped</p><p># # First with space escaped</p>", """
								\\# Escaped
								# \\# One heading
								\\## First escaped
								\\# # First with space escaped""",
						"escaped headings"),
				Arguments.of("<p>*Lorem* _ipsum_ `dolor` ~sit~ \\amet\\</p>",
						"\\*Lorem\\* \\_ipsum\\_ \\`dolor\\` \\~sit\\~ \\\\amet\\\\",
						"unescape escaped characters"),
				Arguments.of("<p>\\\\ \\</p>", "\\\\\\\\ \\\\", "escaped backslashes"),
				Arguments.of("<p>**</p>", "\\*\\*", "empty stars"),
				Arguments.of("<p><strong>** </strong></p>", "**\\*\\* **", "bold stars")
		);
	}
}
