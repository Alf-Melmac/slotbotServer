package de.webalf.slotbot.feature.social_embed;

import de.webalf.slotbot.feature.social_embed.model.OpenGraph;
import de.webalf.slotbot.feature.social_embed.model.SocialEmbedType;
import lombok.NonNull;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Builds Open Graph meta tags for one entity
 *
 * @author Alf
 * @since 02.11.2026
 */
@Service
public class SocialEmbedService {
	private final Map<SocialEmbedType, OpenGraphProvider> providers = new EnumMap<>(SocialEmbedType.class);

	public SocialEmbedService(List<OpenGraphProvider> providers) {
		providers.forEach(provider -> {
			if (this.providers.put(provider.type(), provider) != null) {
				throw new IllegalStateException("Duplicate social meta provider for " + provider.type());
			}
		});
	}

	/**
	 * Returns the meta tags for the given page
	 *
	 * @param type frontend page
	 * @param id   of the entity on the page
	 * @return html meta tags if resolvable
	 */
	public Optional<String> getMetaTags(@NonNull SocialEmbedType type, long id) {
		final OpenGraphProvider provider = providers.get(type);
		if (provider == null) {
			return Optional.empty();
		}
		return provider.resolve(id)
				.map(meta -> buildTags(meta).outerHtml());
	}

	private static Elements buildTags(OpenGraph meta) {
		final Elements tags = new Elements();
		tags.add(openGraphTag("og:site_name", "Slotbot"));
		if (meta.type() != null) {
			tags.add(openGraphTag("og:type", meta.type()));
		}
		tags.add(openGraphTag("og:url", meta.url()));
		tags.add(openGraphTag("og:title", meta.title()));
		if (meta.description() != null) {
			tags.add(openGraphTag("og:description", meta.description()));
		}
		tags.add(openGraphTag("og:image", meta.imageUrl()));
		if (meta.locale() != null) {
			tags.add(openGraphTag("og:locale", meta.locale().toString()));
		}
		tags.add(metaTag("name", "twitter:card", "summary"));
		return tags;
	}

	private static Element openGraphTag(String property, String content) {
		return metaTag("property", property, content);
	}

	private static Element metaTag(String keyAttribute, String key, String content) {
		return new Element("meta")
				.attr(keyAttribute, key)
				.attr("content", content);
	}
}
