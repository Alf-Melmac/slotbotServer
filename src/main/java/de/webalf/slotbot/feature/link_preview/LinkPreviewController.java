package de.webalf.slotbot.feature.link_preview;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alf
 * @since 26.10.2026
 */
@RestController
@RequestMapping("/embed")
@RequiredArgsConstructor
public class LinkPreviewController {
	private final LinkPreviewService linkPreviewService;

	@GetMapping(value = "/events/{eventId}", produces = "text/html;charset=UTF-8")
	public String getEventPreview(@PathVariable String eventId) {
		return linkPreviewService.buildEventPreview(eventId);
	}
}
