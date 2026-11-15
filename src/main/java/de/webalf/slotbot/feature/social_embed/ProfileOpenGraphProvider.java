package de.webalf.slotbot.feature.social_embed;

import de.webalf.slotbot.feature.social_embed.model.OpenGraph;
import de.webalf.slotbot.feature.social_embed.model.SocialEmbedType;
import de.webalf.slotbot.service.external.DiscordBotService;
import de.webalf.slotbot.service.web.RedirectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Alf
 * @since 02.11.2026
 */
@Component
@RequiredArgsConstructor
public class ProfileOpenGraphProvider implements OpenGraphProvider {
	private final DiscordBotService discordBotService;
	private final RedirectService redirectService;

	@Override
	public SocialEmbedType type() {
		return SocialEmbedType.PROFILE;
	}

	/**
	 * Only uses cached Discord users, so crawlers can't trigger (blocking) Discord requests
	 */
	@Override
	public Optional<OpenGraph> resolve(long id) {
		return Optional.ofNullable(discordBotService.getCachedUser(id))
				.map(discordUser -> OpenGraph.builder()
						.title(discordUser.getEffectiveName())
						.url(redirectService.redirectTo("/profile/" + id))
						.imageUrl(discordUser.getAvatarUrl())
						.type("profile")
						.build());
	}
}
