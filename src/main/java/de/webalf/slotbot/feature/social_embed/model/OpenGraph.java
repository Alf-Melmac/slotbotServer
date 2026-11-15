package de.webalf.slotbot.feature.social_embed.model;

import lombok.Builder;
import lombok.NonNull;

import java.util.Locale;

/**
 * Page specific meta information shown in link previews (Open Graph)
 *
 * @param title       <code>og:title</code>
 * @param url         canonical <code>og:url</code>
 * @param description <code>og:description</code>
 * @param imageUrl    absolute <code>og:image</code> url
 * @param type        <code>og:type</code>, defaults to <code>website</code>
 * @param locale      <code>og:locale</code> in format language_TERRITORY
 * @author Alf
 * @since 02.11.2026
 */
@Builder
public record OpenGraph(
		@NonNull String title,
		@NonNull String url,
		String description,
		@NonNull String imageUrl,
		String type,
		Locale locale
) {}
