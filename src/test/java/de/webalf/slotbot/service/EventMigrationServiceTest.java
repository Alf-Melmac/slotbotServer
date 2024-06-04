package de.webalf.slotbot.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alf
 * @since 31.05.2024
 */
class EventMigrationServiceTest {
	@ParameterizedTest
	@MethodSource
	void toHtml(String input, String expected) {
		assertEquals(expected, EventMigrationService.toHtml(input));
	}

	private static Stream<Arguments> toHtml() {
		return Stream.of(
				Arguments.of("Text", "<p>Text</p>"),
				Arguments.of("""
						Text
						break
						""", "<p>Text<br>break</p>"),
				Arguments.of("""
						More

						spaces
						""", "<p>More</p><p>spaces</p>"),
		Arguments.of("_Italic_", "<p><em>Italic</em></p>"),
				Arguments.of("*Italic*", "<p><em>Italic</em></p>"),
				Arguments.of("**Bold**", "<p><strong>Bold</strong></p>"),
				Arguments.of("***bold italics***", "<p><strong><em>bold italics</strong></em></p>"),
				Arguments.of("__underline__", "<p><u>underline</u></p>"),
				Arguments.of("__*underline italics*__", "<p><u><em>underline italics</em></u></p>"),
				Arguments.of("__**underline bold**__", "<p><u><strong>underline bold</strong></u></p>"),
				Arguments.of("__***underline bold italics***__", "<p><u><strong><em>underline bold italics</strong></em></u></p>"),
				Arguments.of("~~strikethrough~~", "<p><s>strikethrough</s></p>"),
				Arguments.of("*italics* or _italics_ **bold** ***bold italics*** __underline__ __*underline italics*__ __**underline bold**__ __***underline bold italics***__ ~~strikethrough~~",
						"<p><em>italics</em> or <em>italics</em> <strong>bold</strong> <strong><em>bold italics</strong></em> <u>underline</u> <u><em>underline italics</em></u> <u><strong>underline bold</strong></u> <u><strong><em>underline bold italics</strong></em></u> <s>strikethrough</s></p>"),
				Arguments.of("# Heading 1", "<h1>Heading 1</h1>"),
				Arguments.of("## Heading 2", "<h2>Heading 2</h2>"),
				Arguments.of("### Heading 3", "<h3>Heading 3</h3>"),
				Arguments.of("""
						# Hello World
						Text""", "<h1>Hello World</h1><p>Text</p>"),
				Arguments.of("""
						Text
						# Hello World""", "<p>Text</p><h1>Hello World</h1>"),
				Arguments.of("""
						# Heading 1
						h1
						## Heading 2
						h2

						### Heading 3
						h3""", "<h1>Heading 1</h1><p>h1</p><h2>Heading 2</h2><p>h2</p><p></p><h3>Heading 3</h3><p>h3</p>"),
				Arguments.of("Text # Hello World", "<p>Text # Hello World</p>"),
				Arguments.of("""
						\\# Escaped
						# \\# One heading
						#\\# Missing space
						\\## First escaped
						\\# # First with space escaped
						\\#\\# Both escaped
						\\# \\# More escaped""", "<p># Escaped</p><h1># One heading</h1><p>## Missing space<br>## First escaped<br># # First with space escaped<br>## Both escaped<br># # More escaped</p>"),
				Arguments.of("\\*Lorem\\* \\_ipsum\\_ \\`dolor\\` \\~sit\\~ \\\\amet\\\\", "<p>*Lorem* _ipsum_ `dolor` ~sit~ \\amet\\</p>"),
				Arguments.of("\\\\\\\\ \\\\", "<p>\\\\ \\</p>"),
				Arguments.of("**", "<p>**</p>"),
				Arguments.of("**** **", "<p><strong>** </strong></p>")
		);
	}
}
