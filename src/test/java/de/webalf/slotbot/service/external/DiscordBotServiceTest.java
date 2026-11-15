package de.webalf.slotbot.service.external;

import de.webalf.slotbot.model.external.discord.DiscordUser;
import de.webalf.slotbot.service.bot.BotService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import static de.webalf.slotbot.service.external.DiscordBotService.USER_CACHE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Alf
 * @since 07.11.2026
 */
@ExtendWith(MockitoExtension.class)
class DiscordBotServiceTest {
	private static final long USER_ID = 42L;
	private static final String USER_NAME = "user";

	@Mock
	private BotService botService;
	@Mock
	private JDA jda;

	private CacheManager cacheManager;
	private DiscordBotService sut;

	@BeforeEach
	void setUp() {
		cacheManager = new ConcurrentMapCacheManager();
		sut = new DiscordBotService(botService, null, cacheManager);
	}

	@Test
	void getCachedUserFromUserCache() {
		final DiscordUser cachedUser = DiscordUser.builder().id(USER_ID).username(USER_NAME).build();
		//noinspection DataFlowIssue ConcurrentMapCacheManager creates caches on demand
		cacheManager.getCache(USER_CACHE).put(USER_ID, cachedUser);

		assertThat(sut.getCachedUser(USER_ID)).isEqualTo(cachedUser);
		verifyNoInteractions(botService);
	}

	@Test
	void getCachedUserFromJda() {
		when(botService.getJda()).thenReturn(jda);
		final User jdaUser = mock(User.class);
		when(jda.getUserById(USER_ID)).thenReturn(jdaUser);
		when(jdaUser.getIdLong()).thenReturn(USER_ID);
		when(jdaUser.getName()).thenReturn(USER_NAME);

		assertThat(sut.getCachedUser(USER_ID))
				.extracting(DiscordUser::id, DiscordUser::username)
				.containsExactly(USER_ID, USER_NAME);
	}

	@Test
	void getCachedUserNotCached() {
		when(botService.getJda()).thenReturn(jda);

		assertThat(sut.getCachedUser(USER_ID)).isNull();
		verify(jda, only()).getUserById(USER_ID);
	}

	@Test
	void getCachedUserNotCachedWithoutJda() {
		assertThat(sut.getCachedUser(USER_ID)).isNull();
	}
}
