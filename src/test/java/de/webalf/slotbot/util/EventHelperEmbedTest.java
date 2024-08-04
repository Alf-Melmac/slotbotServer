package de.webalf.slotbot.util;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.EventField;
import de.webalf.slotbot.model.EventType;
import de.webalf.slotbot.model.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static de.webalf.slotbot.util.MockUtils.TEST_LOCALE;
import static de.webalf.slotbot.util.MockUtils.mockMessage;
import static java.time.ZoneOffset.UTC;
import static net.dv8tion.jda.api.utils.TimeFormat.DATE_TIME_SHORT;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alf
 * @since 10.04.2023
 */
@ExtendWith(MockitoExtension.class)
class EventHelperEmbedTest {
	private static final String EVENT_TYPE_COLOR = "#000000";
	private static final String EVENT_TYPE_NAME = "event type name";
	private static final EventType EVENT_TYPE = EventType.builder().color(EVENT_TYPE_COLOR).name(EVENT_TYPE_NAME).build();
	private static final String NAME = "name";
	private static final int ID = -123;
	private static final String GUILD_BASE_URL = "https://example.net";
	private static final Guild OWNER_GUILD = Guild.builder().id(-1234).baseUrl(GUILD_BASE_URL).build();
	private static final String DESCRIPTION = "description";
	private static final String PICTURE_URL = "https://example.org";
	private static final String CREATOR = "creator";
	private static final LocalDateTime DATE = LocalDateTime.of(2023, 4, 26, 12, 38);
	private static final String FOOTER = "footer";
	private static final Event.EventBuilder<?, ?> MINIMAL_EVENT = Event.builder()
			.eventType(EVENT_TYPE)
			.ownerGuild(OWNER_GUILD)
			.creator(CREATOR)
			.dateTime(DATE)
			.details(Collections.emptyList());
	private static final String FIELD_NAME_SCHEDULE = "schedule";
	private static final String FIELD_NAME_MISSION_TYPE = "missionType";
	private static final String FIELD_NAME_RESERVE = "reserve";

	@Mock
	MessageSource messageSource;

	@InjectMocks
	EventHelper sut;

	@BeforeEach
	void setUp() {
		mockMessage(messageSource, FOOTER, "bot.embed.event.footer", EVENT_TYPE_NAME, CREATOR);
		mockMessage(messageSource, FIELD_NAME_SCHEDULE, "bot.embed.event.details.schedule");
		mockMessage(messageSource, FIELD_NAME_MISSION_TYPE, "bot.embed.event.details.missionType");
		mockMessage(messageSource, FIELD_NAME_RESERVE, "bot.embed.event.details.reserveParticipating");
	}

	@Test
	void buildDetailsEmbedSetsStandardFields() {
		final Event event = Event.builder()
				.eventType(EVENT_TYPE)
				.name(NAME)
				.id(ID)
				.ownerGuild(OWNER_GUILD)
				.description(DESCRIPTION)
				.pictureUrl(PICTURE_URL)
				.creator(CREATOR)
				.dateTime(LocalDateTime.now())
				.details(Collections.emptyList())
				.build();

		final MessageEmbed result = sut.buildDetailsEmbed(event, TEST_LOCALE);

		assertAll(
				() -> assertEquals(EVENT_TYPE_COLOR, "#" + Integer.toHexString(result.getColor().getRGB()).substring(2)),
				() -> assertEquals(NAME, result.getTitle()),
				() -> assertEquals(GUILD_BASE_URL + "/events/" + ID, result.getUrl()),
				() -> assertEquals(DESCRIPTION, result.getDescription()),
				() -> assertEquals(PICTURE_URL, result.getThumbnail().getUrl()),
				() -> assertEquals(FOOTER, result.getFooter().getText()),
				() -> assertThat(Instant.now()).isCloseTo(result.getTimestamp().toInstant(), within(10, ChronoUnit.SECONDS))
		);
	}

	@Test
	void buildDetailsEmbedAddsHiddenImage() {
		final Event event = MINIMAL_EVENT.build();

		event.setHidden(true);

		final MessageEmbed result = sut.buildDetailsEmbed(event, TEST_LOCALE);

		assertEquals("https://slotbot.de/backend/userContent/1/hidden_event.jpg", result.getImage().getUrl());
	}

	@Test
	void buildDetailsEmbedAddsStandardFields() {
		final String missionType = "mission type";
		final Event event = MINIMAL_EVENT
				.dateTime(DATE)
				.missionType(missionType)
				.build();

		final MessageEmbed result = sut.buildDetailsEmbed(event, TEST_LOCALE);

		assertThat(result.getFields())
				.hasSize(2)
				.extracting("name", "value", "inline")
				.containsExactly(
						tuple(FIELD_NAME_SCHEDULE, DATE_TIME_SHORT.format(DATE.atZone(UTC)) + " Uhr", false),
						tuple(FIELD_NAME_MISSION_TYPE, missionType, true)
				);
	}

	@Test
	void buildDetailsEmbedAddsMissionLength() {
		final String missionLength = "mission length";
		final Event event = MINIMAL_EVENT
				.dateTime(DATE)
				.missionLength(missionLength)
				.build();

		final String dateText = DATE_TIME_SHORT.format(DATE.atZone(UTC)) + " Uhr";
		final String translatedDateText = "translatedDateText";
		mockMessage(messageSource, translatedDateText, "bot.embed.event.details.schedule.text", dateText, missionLength);
		final MessageEmbed result = sut.buildDetailsEmbed(event, TEST_LOCALE);

		assertThat(result.getFields())
				.filteredOn(field -> FIELD_NAME_SCHEDULE.equals(field.getName()))
				.hasSize(1)
				.extracting("value", "inline")
				.containsExactly(
						tuple(translatedDateText, false)
				);
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void buildDetailsEmbedAddsReserveParticipating(boolean reserveParticipating) {
		final Event event = MINIMAL_EVENT
				.reserveParticipating(reserveParticipating)
				.build();

		final String key = reserveParticipating ? "yes" : "no";
		mockMessage(messageSource, key, key);

		final MessageEmbed result = sut.buildDetailsEmbed(event, TEST_LOCALE);

		assertThat(result.getFields())
				.filteredOn(field -> FIELD_NAME_RESERVE.equals(field.getName()))
				.hasSize(1)
				.extracting("value", "inline")
				.containsExactly(
						tuple(key, true)
				);
	}

	@Test
	void buildDetailsEmbedTransformsBooleanFields() {
		final Event event = MINIMAL_EVENT
				.details(List.of(
						EventField.builder().title("event field title1").text("event field text").build(),
						EventField.builder().title("event field title2").text("true").build(),
						EventField.builder().title("event field title3").text("false").build()
				))
				.build();
		mockMessage(messageSource, "yes", "yes");
		mockMessage(messageSource, "no", "no");

		final MessageEmbed result = sut.buildDetailsEmbed(event, TEST_LOCALE);

		assertThat(result.getFields())
				.filteredOn(field -> field.getName().startsWith("event field title"))
				.hasSize(3)
				.extracting("name", "value", "inline")
				.containsExactly(
						tuple("event field title1", "event field text", true),
						tuple("event field title2", "yes", true),
						tuple("event field title3", "no", true)
				);
	}

	@Test
	void buildDetailsEmbedTransformsLinkField() {
		final String eventFieldTitle = "Modset";
		final String eventFieldText = "event field text";

		final String eventFieldLink = "event field link";
		final Event event = MINIMAL_EVENT
				.squadList(Collections.emptyList())
				.details(List.of(EventField.builder()
						.title(eventFieldTitle)
						.text(eventFieldText)
						.build()))
				.build();
		event.setBackReferences();

		final MessageEmbed result;
		try (MockedStatic<Arma3ModsetUtils> fieldUtils = Mockito.mockStatic(Arma3ModsetUtils.class)) {
			fieldUtils.when(() -> Arma3ModsetUtils.getModSetUrl(eventFieldText, GUILD_BASE_URL))
					.thenReturn(eventFieldLink);

			result = sut.buildDetailsEmbed(event, TEST_LOCALE);
		}

		assertThat(result.getFields())
				.filteredOn(field -> eventFieldTitle.equals(field.getName()))
				.hasSize(1)
				.extracting("value", "inline")
				.containsExactly(
						tuple("[" + eventFieldText + "](" + eventFieldLink + ")", true)
				);
	}
}
