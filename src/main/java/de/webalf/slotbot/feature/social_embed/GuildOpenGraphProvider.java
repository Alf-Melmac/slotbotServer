package de.webalf.slotbot.feature.social_embed;

import de.webalf.slotbot.feature.social_embed.model.OpenGraph;
import de.webalf.slotbot.feature.social_embed.model.SocialEmbedType;
import de.webalf.slotbot.repository.GuildRepository;
import de.webalf.slotbot.service.GuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Objects.requireNonNullElseGet;

/**
 * @author Alf
 * @since 02.11.2026
 */
@Component
@RequiredArgsConstructor
public class GuildOpenGraphProvider implements OpenGraphProvider {
	private final GuildRepository guildRepository;

	@Override
	public SocialEmbedType type() {
		return SocialEmbedType.GUILDS;
	}

	@Override
	public Optional<OpenGraph> resolve(long id) {
		return guildRepository.findById(id)
				.map(guild -> OpenGraph.builder()
						.title(guild.getGroupIdentifier())
						.url(guild.getBaseRedirectUrl() + "/guilds/" + guild.getId())
						.imageUrl(requireNonNullElseGet(guild.buildEmojiUrl(), () -> GuildService.getLogo(guild)))
						.locale(guild.getLocale())
						.build());
	}
}
