package de.webalf.slotbot.feature.link_preview;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.service.EventService;
import de.webalf.slotbot.service.web.RedirectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Alf
 * @since 26.10.2026
 */
@ExtendWith(MockitoExtension.class)
class LinkPreviewControllerTest {
	private static final String GENERIC_PREVIEW_TITLE = "<meta property=\"og:title\" content=\"Slotbot\"/>";

	@Mock
	EventService eventService;
	@Mock
	RedirectService redirectService;

	MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		when(redirectService.redirectTo(anyString())).thenAnswer(invocation -> "https://slotbot.de" + invocation.getArgument(0));
		mockMvc = MockMvcBuilders
				.standaloneSetup(new LinkPreviewController(new LinkPreviewService(eventService, redirectService)))
				.build();
	}

	@Test
	void publicEventReturnsEventTags() throws Exception {
		when(eventService.findOptionalById(42)).thenReturn(Optional.of(buildEvent("Operation Alpha", "Coop", false)));

		assertThat(getPreview("42"))
				.contains("<meta property=\"og:title\" content=\"Operation Alpha\"/>")
				.contains("<meta property=\"og:url\" content=\"https://slotbot.de/events/42\"/>")
				//18:00 UTC -> 20:00 Europe/Berlin (summer time)
				.contains("<meta property=\"og:description\" content=\"Sa., 04.07.2026, 20:00 Uhr · Coop\"/>")
				.contains("<meta property=\"og:image\" content=\"https://example.org/picture.png\"/>");
	}

	@Test
	void hiddenEventIsIndistinguishableFromUnknownEvent() throws Exception {
		when(eventService.findOptionalById(42)).thenReturn(Optional.of(buildEvent("Secret Operation", "Coop", true)));
		when(eventService.findOptionalById(999)).thenReturn(Optional.empty());

		final String hiddenPreview = getPreview("42");

		assertThat(hiddenPreview)
				.contains(GENERIC_PREVIEW_TITLE)
				.doesNotContain("Secret Operation")
				.isEqualTo(getPreview("999"));
	}

	@ParameterizedTest
	@ValueSource(strings = {"new", "<script>", "99999999999999999999"})
	void invalidIdReturnsGenericTags(String eventId) throws Exception {
		assertThat(getPreview(eventId))
				.contains(GENERIC_PREVIEW_TITLE)
				.doesNotContain("<script>");
	}

	@Test
	void eventValuesAreEscaped() throws Exception {
		when(eventService.findOptionalById(42)).thenReturn(Optional.of(buildEvent("<script>\"x\" & 'y'", "\"><script>", false)));

		assertThat(getPreview("42"))
				.doesNotContain("<script>")
				.contains("<meta property=\"og:title\" content=\"&lt;script&gt;&quot;x&quot; &amp; &#39;y&#39;\"/>")
				.contains("· &quot;&gt;&lt;script&gt;\"/>");
	}

	private String getPreview(String eventId) throws Exception {
		return mockMvc.perform(get("/embed/events/{eventId}", eventId))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/html;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
	}

	private static Event buildEvent(String name, String eventTypeName, boolean hidden) {
		return Event.builder()
				.id(42)
				.name(name)
				.hidden(hidden)
				.eventType(EventType.builder().color("#000000").name(eventTypeName).build())
				.ownerGuild(Guild.builder().id(-1234).build())
				.dateTime(LocalDateTime.of(2026, 7, 4, 18, 0))
				.pictureUrl("https://example.org/picture.png")
				.build();
	}
}
