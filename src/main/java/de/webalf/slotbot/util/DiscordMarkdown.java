package de.webalf.slotbot.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.util.ArrayDeque;
import java.util.Deque;

import static de.webalf.slotbot.util.StringUtils.isEmpty;
import static java.lang.Boolean.TRUE;

/**
 * Util class to work with discord formatting
 *
 * @author Alf
 * @see <a href="https://support.discord.com/hc/en-us/articles/210298617" target"_top">https://support.discord.com/hc/en-us/articles/210298617</a>
 * @since 12.11.2020
 */
@UtilityClass
@Slf4j
public final class DiscordMarkdown {
	/**
	 * Imitates the discord markup by replacing html with discord markdown
	 *
	 * @return marked down string
	 */
	public static String toMarkdown(String s) {
		if (isEmpty(s)) {
			return null;
		}

		final DiscordVisitor visitor = new DiscordVisitor();
		final Document document = Jsoup.parse(s);
		NodeTraversor.traverse(visitor, document.body());
		return visitor.getResult();
	}

	private static class DiscordVisitor implements NodeVisitor {
		private final StringBuilder result = new StringBuilder();
		/**
		 * Stack to keep track of the potentially nested lists.
		 * The top of the stack describes the list type of the current level
		 */
		private final Deque<Boolean> unorderedListStack = new ArrayDeque<>();

		@Override
		public void head(@NonNull Node node, int depth) {
			if (node instanceof final Element element) {
				switch (element.tagName()) {
					case "h1" -> result.append("# ");
					case "h2" -> result.append("## ");
					case "h3" -> result.append("### ");
					case "ul" -> {
						if (!unorderedListStack.isEmpty()) { // Add a line break before starting a nested list
							result.append("\n");
						}
						unorderedListStack.push(true);
					}
					case "ol" -> {
						if (!unorderedListStack.isEmpty()) { // Add a line break before starting a nested list
							result.append("\n");
						}
						unorderedListStack.push(false);
					}
					case "li" -> result
							.append(" ".repeat(unorderedListStack.size() - 1))
							.append(TRUE.equals(unorderedListStack.peek()) ? "- " : "1. ");
					case "strong" -> result.append("**");
					case "em" -> result.append("*");
					case "u" -> result.append("__");
					case "s" -> result.append("~~");
				}
			}
		}

		@Override
		public void tail(@NonNull Node node, int depth) {
			if (node instanceof final Element element) {
				switch (element.tagName()) {
					case "h1", "h2", "h3", "p" -> {
						if (unorderedListStack.isEmpty()) { // Do not add line breaks inside lists
							result.append("\n");
						}
					}
					case "ul", "ol" -> unorderedListStack.remove();
					case "li" -> {
						// Don't add a line break if the last item was already a closing list item
						if (result.charAt(result.length() - 1) != '\n') {
							result.append("\n");
						}
					}
					case "strong" -> result.append("**");
					case "em" -> result.append("*");
					case "u" -> result.append("__");
					case "s" -> result.append("~~");
				}
			} else if (node instanceof final TextNode textNode) {
				String wholeText = textNode.getWholeText();
				if (!unorderedListStack.isEmpty()) { //Remove potential line breaks from the list wrapper items
					wholeText = wholeText.replaceAll("\n\\s*", "");
				}
				result.append(escape(wholeText));
			}
		}

		public String getResult() {
			//Remove the last \n if it exists
			if (!result.isEmpty() && result.charAt(result.length() - 1) == '\n') {
				result.deleteCharAt(result.length() - 1);
			}
			return result.toString();
		}

		private static String escape(String s) {
			return s.replaceAll("([*_`~\\\\])", "\\\\$1")
					.replaceFirst("^((?:#+|-)\\s)", "\\\\$1")
					.replaceFirst("^(\\d)(\\.\\s)", "$1\\\\$2");
		}
	}
}
