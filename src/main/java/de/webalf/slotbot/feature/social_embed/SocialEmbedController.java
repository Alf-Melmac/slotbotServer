package de.webalf.slotbot.feature.social_embed;

import de.webalf.slotbot.feature.social_embed.model.SocialEmbedType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

/**
 * Page specific meta tag provider
 *
 * @author Alf
 * @since 02.11.2026
 */
@RestController
@RequestMapping("/embed")
@RequiredArgsConstructor
public class SocialEmbedController {
	private final SocialEmbedService socialEmbedService;

	/**
	 * @param type type of entity
	 * @param id   id of entity
	 * @return meta tags or no content if there is no embed for this combination
	 */
	@GetMapping(value = "/{type}/{id}", produces = TEXT_HTML_VALUE)
	public ResponseEntity<String> getMetaTags(@PathVariable(value = "type") String type,
											  @PathVariable(value = "id") long id) {
		return SocialEmbedType.fromPath(type)
				.flatMap(embedType -> socialEmbedService.getMetaTags(embedType, id))
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.noContent().build());
	}
}
