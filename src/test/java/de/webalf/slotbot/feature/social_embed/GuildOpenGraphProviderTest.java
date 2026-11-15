package de.webalf.slotbot.feature.social_embed;

import de.webalf.slotbot.feature.social_embed.model.OpenGraph;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.enums.Language;
import de.webalf.slotbot.repository.GuildRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Alf
 * @since 15.11.2026
 */
@ExtendWith(MockitoExtension.class)
class GuildOpenGraphProviderTest {
	private static final long GUILD_ID = 1L;

	@Mock
	private GuildRepository guildRepository;

	@InjectMocks
	private GuildOpenGraphProvider sut;

	@ParameterizedTest
	@EnumSource(Language.class)
	void localeHasLanguageTerritoryFormat(Language language) {
		when(guildRepository.findById(GUILD_ID)).thenReturn(Optional.of(Guild.builder().id(GUILD_ID).language(language).build()));

		assertThat(sut.resolve(GUILD_ID))
				.map(OpenGraph::locale)
				.hasValueSatisfying(locale -> assertThat(locale.toString())
						.matches("[a-z]{2}_[A-Z]{2}")
						.startsWith(language.name().toLowerCase() + "_"));
	}
}
