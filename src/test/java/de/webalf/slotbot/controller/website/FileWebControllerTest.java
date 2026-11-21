package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.service.FileService;
import de.webalf.slotbot.service.GuildService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * @author Alf
 * @since 21.11.2026
 */
@ExtendWith(MockitoExtension.class)
class FileWebControllerTest {
	@Mock
	private FileService fileService;
	@Mock
	private GuildService guildService;

	@InjectMocks
	private FileWebController sut;

	@ParameterizedTest
	@ValueSource(strings = {"42.ics", "-1.ics"})
	void getCalendarServesCalendarFileWithoutGuildLookup(String filename) {
		when(fileService.loadIcsAsResource(filename)).thenReturn(new ByteArrayResource(new byte[0]));

		sut.getCalendar(filename);

		verify(fileService).loadIcsAsResource(filename);
		verifyNoInteractions(guildService);
	}

	@Test
	void getCalendarResolvesGuildName() {
		when(guildService.findByName("Foo")).thenReturn(Optional.of(Guild.builder().id(42).build()));
		when(fileService.loadIcsAsResource("42.ics")).thenReturn(new ByteArrayResource(new byte[0]));

		sut.getCalendar("Foo");

		verify(fileService).loadIcsAsResource("42.ics");
	}

	@Test
	void getCalendarFallsBackToFilenameForUnknownGuild() {
		when(guildService.findByName("unknown")).thenReturn(Optional.empty());
		when(fileService.loadIcsAsResource("unknown")).thenReturn(new ByteArrayResource(new byte[0]));

		sut.getCalendar("unknown");

		verify(fileService).loadIcsAsResource("unknown");
	}
}
