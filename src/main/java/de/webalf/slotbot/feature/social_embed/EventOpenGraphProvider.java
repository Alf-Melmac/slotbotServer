package de.webalf.slotbot.feature.social_embed;

import de.webalf.slotbot.feature.social_embed.model.OpenGraph;
import de.webalf.slotbot.feature.social_embed.model.SocialEmbedType;
import de.webalf.slotbot.repository.EventRepository;
import de.webalf.slotbot.util.EventUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Alf
 * @since 02.11.2026
 */
@Component
@RequiredArgsConstructor
public class EventOpenGraphProvider implements OpenGraphProvider {
	private final EventRepository eventRepository;

	@Override
	public SocialEmbedType type() {
		return SocialEmbedType.EVENTS;
	}

	@Override
	public Optional<OpenGraph> resolve(long id) {
		// TODO findById eagerly loads the whole event including squads and slots. Use a projection of name, description, picture and owner guild instead
		return eventRepository.findById(id)
				.map(event -> OpenGraph.builder()
						.title(event.getName())
						.url(EventUtils.buildUrl(event))
						.description(StringUtils.abbreviate(toPlainText(event.getDescription()), 200))
						.imageUrl(event.getPictureUrlWithDefault())
						.locale(event.getOwnerGuildLocale())
						.build());
	}

	/**
	 * The description is rich text, but link previews only show plain text
	 */
	private static String toPlainText(String html) {
		return html != null ? StringUtils.trimToNull(Jsoup.parse(html).text()) : null;
	}
}
