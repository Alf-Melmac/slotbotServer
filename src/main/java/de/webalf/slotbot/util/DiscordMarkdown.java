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
	/**
	 * Imitates the discord markup by replacing the style symbols with html tags
	 *
	 * @return marked down string
	 */
	@Deprecated
	public static String toHtml(String s) {
		return null;
	}

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

		@Override
		public void head(@NonNull Node node, int depth) {
			if (node instanceof final Element element) {
				switch (element.tagName()) {
					case "h1":
						result.append("# ");
						break;
					case "h2":
						result.append("## ");
						break;
					case "h3":
						result.append("### ");
						break;
					case "strong":
						result.append("**");
						break;
					case "em":
						result.append("*");
						break;
					case "u":
						result.append("__");
						break;
					case "s":
						result.append("~~");
						break;
				}
			}
		}

		@Override
		public void tail(@NonNull Node node, int depth) {
			if (node instanceof final Element element) {
				switch (element.tagName()) {
					case "strong":
						result.append("**");
						break;
					case "em":
						result.append("*");
						break;
					case "u":
						result.append("__");
						break;
					case "s":
						result.append("~~");
						break;
				}
			} else if (node instanceof final TextNode textNode) {
				result.append(escape(textNode.getWholeText()));
			}
		}

		public String getResult() {
			return result.toString();
		}

		private static String escape(String s) {
			return s.replaceAll("([*_~\\\\])", "\\\\$1")
					.replaceFirst("^#", "\\\\#");
		}
	}
}
