package de.webalf.slotbot.model;

import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.feature.notifications.EventNotificationService;
import de.webalf.slotbot.util.bot.MentionUtils;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static de.webalf.slotbot.AssertionUtils.assertMessageEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

/**
 * @author Alf
 * @since 20.10.2021
 */
class EventTest {
	//estimateEventLength
	@ParameterizedTest
	@MethodSource
	void estimateEventLength(String eventLength, Duration expected) {
		final Event sut = Event.builder()
				.missionLength(eventLength)
				.build();

		assertThat(sut.estimateEventLength()).isEqualTo(expected);
	}

	private static final Duration DEFAULT_DURATION = Duration.ofHours(1);

	private static Stream<Arguments> estimateEventLength() {
		return Stream.of(
				Arguments.of("1h", Duration.ofHours(1)),
				Arguments.of("1 hour", Duration.ofHours(1)),
				Arguments.of("2 hours", Duration.ofHours(2)),
				Arguments.of("2h 30m", Duration.ofHours(2).plusMinutes(30)),
				Arguments.of("45m", Duration.ofMinutes(45)),
				Arguments.of("2-3h", Duration.ofHours(2)),
				Arguments.of("2.5h", Duration.ofHours(2).plusMinutes(30)),
				Arguments.of("11-12 Stunden", Duration.ofHours(11)),
				Arguments.of("11 Stunden und 2 Minuten", Duration.ofHours(11).plusMinutes(2)),
				Arguments.of("11h", Duration.ofHours(11)),
				Arguments.of("1 Stunde und 20 Minuten", Duration.ofHours(1).plusMinutes(20)),
				Arguments.of("2 Stunden und 1 Minute", Duration.ofHours(2).plusMinutes(1)),
				Arguments.of("more than 3 hours", Duration.ofHours(3)),
				Arguments.of("1.5h", Duration.ofHours(1).plusMinutes(30)),
				Arguments.of("90m", Duration.ofMinutes(90)),
				Arguments.of("90 Minuten", Duration.ofMinutes(90)),
				Arguments.of("0h", Duration.ofHours(0)),
				Arguments.of("3–4 Stunden", Duration.ofHours(3)),
				Arguments.of("3—4 Stunden", Duration.ofHours(3)),
				Arguments.of("3~4h", Duration.ofHours(3)),
				Arguments.of("10 min", Duration.ofMinutes(10)),
				Arguments.of("2h 0m", Duration.ofHours(2)),
				Arguments.of("0,5 Stunden", Duration.ofMinutes(30)),
				//Fallback to 1 hour for no guess
				Arguments.of("3pm", DEFAULT_DURATION),
				Arguments.of("14 o'clock", DEFAULT_DURATION),
				Arguments.of("3 AM", DEFAULT_DURATION),
				Arguments.of("12.00", DEFAULT_DURATION),
				Arguments.of("invalid", DEFAULT_DURATION),
				Arguments.of("Wir sind 7 Zwerge", DEFAULT_DURATION),
				Arguments.of(null, DEFAULT_DURATION),
				Arguments.of("", DEFAULT_DURATION),
				Arguments.of("   ", DEFAULT_DURATION),

				//Real world examples
				Arguments.of("3 hours", Duration.ofHours(3)),
				Arguments.of("Bis zu 4 Stunden", Duration.ofHours(4)),
				Arguments.of("3 bis 4 Stunden", Duration.ofHours(3)),
				Arguments.of("2 Stunden", Duration.ofHours(2)),
				Arguments.of("14 Stunden", Duration.ofHours(14)),
				Arguments.of("3-4h", Duration.ofHours(3)),
				Arguments.of("24 Stunden", Duration.ofHours(24)),
				Arguments.of("max. 3 Stunden", Duration.ofHours(3)),
				Arguments.of("6 Stunden", Duration.ofHours(6)),
				Arguments.of("über 3 Stunden", Duration.ofHours(3)),
				Arguments.of("max 2h", Duration.ofHours(2)),
				Arguments.of("552 Stunden", Duration.ofHours(552)),
				Arguments.of("ca. 4 Std.[+/-] je nach Spieleranzahl und Wunsch", Duration.ofHours(4)),
				Arguments.of("über 3 Stunden (Hauptevent)", Duration.ofHours(3)),
				Arguments.of("2-3h", Duration.ofHours(2)),
				Arguments.of("4 Sunden", Duration.ofHours(4)),
				Arguments.of("30 Minuten", Duration.ofMinutes(30)),
				Arguments.of("4", Duration.ofHours(4)),
				Arguments.of("2,5h (+1,5h)", Duration.ofHours(2).plusMinutes(30)),
				Arguments.of("2-3 Stunden", Duration.ofHours(2)),
				Arguments.of("über 30 Minuten", Duration.ofMinutes(30)),
				Arguments.of("1-2h max!", Duration.ofHours(1)),
				Arguments.of("3 Stunden +/-", Duration.ofHours(3)),
				Arguments.of("3 Stunden (22 Uhr / 22:30)", Duration.ofHours(3)),
				Arguments.of("3 Stunden plus Pause", Duration.ofHours(3)),
				Arguments.of("bis zu 3 Stunden", Duration.ofHours(3)),
				Arguments.of("3 Stunden+-", Duration.ofHours(3)),
				Arguments.of("2 bis 3 Stunden", Duration.ofHours(2)),
				Arguments.of("over 4 hours", Duration.ofHours(4)),
				Arguments.of("8 Stunden", Duration.ofHours(8)),
				Arguments.of("1 Stunden", Duration.ofHours(1)),
				Arguments.of("über 4 Stunden", Duration.ofHours(4)),
				Arguments.of("3 - 4 Stunden", Duration.ofHours(3)),
				Arguments.of("2-3h je nach Lust und Laune", Duration.ofHours(2)),
				Arguments.of("2 - 3 Stunden", Duration.ofHours(2)),
				Arguments.of("4 hours", Duration.ofHours(4)),
				Arguments.of("1,5 Stunden", Duration.ofHours(1).plusMinutes(30)),
				Arguments.of("4 Stunden +/-", Duration.ofHours(4)),
				Arguments.of("1 Stunde", Duration.ofHours(1)),
				Arguments.of("4h", Duration.ofHours(4)),
				Arguments.of("2,5 Stunden", Duration.ofHours(2).plusMinutes(30)),
				Arguments.of("ca. 3-4 Stunden", Duration.ofHours(3)),
				Arguments.of("5h", Duration.ofHours(5)),
				Arguments.of("2 hours", Duration.ofHours(2)),
				Arguments.of("30 Minuten, danach offene Gesprächsrunde.", Duration.ofMinutes(30)),
				Arguments.of("über 2 Stunden", Duration.ofHours(2)),
				Arguments.of("max. 4 Stunden", Duration.ofHours(4)),
				Arguments.of("3-4 Stunden", Duration.ofHours(3)),
				Arguments.of("3 1/2 Stunden", Duration.ofHours(3).plusMinutes(30)),
				Arguments.of("72h", Duration.ofHours(72)),
				Arguments.of("3 Stunden (maximal 00.00 Uhr)", Duration.ofHours(3)),
				Arguments.of("12 Stunden", Duration.ofHours(12)),
				Arguments.of("3 Stunden", Duration.ofHours(3)),
				Arguments.of("5 Stunden", Duration.ofHours(5)),
				Arguments.of("2-4 Stunden", Duration.ofHours(2)),
				Arguments.of("30-60 Minuten, danach offene Gesprächsrunde", Duration.ofMinutes(30)),
				Arguments.of("ca. 1 Stunde", Duration.ofHours(1)),
				Arguments.of("ca. 2-3h", Duration.ofHours(2)),
				Arguments.of("16 Stunden", Duration.ofHours(16)),
				Arguments.of("3,5 Stunden", Duration.ofHours(3).plusMinutes(30)),
				Arguments.of("3 1/2 - 4 Stunden", Duration.ofHours(3).plusMinutes(30)),
				Arguments.of("4 Stunden", Duration.ofHours(4)),
				Arguments.of("über 4 Stunden (Hauptevent)", Duration.ofHours(4)),
				Arguments.of("3 Stunden +", Duration.ofHours(3)),
				Arguments.of("3+ Stunden", Duration.ofHours(3)),

				//Fallback to 1 hour for Times
				Arguments.of("bis 22 Uhr", DEFAULT_DURATION),
				Arguments.of("bis 23:30", DEFAULT_DURATION),
				Arguments.of("max. 22:30", DEFAULT_DURATION),
				Arguments.of("bis 21:30", DEFAULT_DURATION),
				Arguments.of("Maximal 22:30", DEFAULT_DURATION),
				Arguments.of("maximal 22:30", DEFAULT_DURATION),
				Arguments.of("bis ca. 23:30", DEFAULT_DURATION),
				Arguments.of("Maximal bis 24:00", DEFAULT_DURATION),
				//Fallback to 1 hour for no estimate
				Arguments.of("Bis keiner Lust mehr hat", DEFAULT_DURATION),
				Arguments.of("Oben end", DEFAULT_DURATION),
				Arguments.of("kein Plan ", DEFAULT_DURATION),
				Arguments.of("Solange wir Spass haben.", DEFAULT_DURATION)
		);
	}

	//findSquadByPosition
	@Test
	void findSquadByPosition() {
		final Squad squad1 = Squad.builder().name("Squad 1").build();
		final Squad squad2 = Squad.builder().name("Squad 2").build();
		final Squad squad3 = Squad.builder().name("Squad 3").build();
		final Event sut = buildEventWithSquads(squad1, squad2, squad3);

		assertThat(sut.findSquadByPosition(0)).isEqualTo(squad1);
		assertThat(sut.findSquadByPosition(1)).isEqualTo(squad2);
		assertThat(sut.findSquadByPosition(2)).isEqualTo(squad3);
	}

	@Test
	void findSquadByPositionWithOneSquad() {
		final Squad squad = Squad.builder().name("Squad").build();
		final Event sut = buildEventWithSquads(squad);

		assertThat(sut.findSquadByPosition(0)).isEqualTo(squad);
	}

	@Test
	void findSquadByPositionWithEmptyList() {
		final Event sut = buildEventWithSquads();

		final BusinessRuntimeException exception = assertThrows(BusinessRuntimeException.class, () -> sut.findSquadByPosition(0));
		assertMessageEquals("Couldn't find a squad on position 0.", exception);
	}

	@Test
	void findSquadByPositionWithNegative() {
		final Event sut = buildEventWithSquads(Squad.builder().name("Squad").build());

		final BusinessRuntimeException exception = assertThrows(BusinessRuntimeException.class, () -> sut.findSquadByPosition(-1));
		assertMessageEquals("Couldn't find a squad on position -1.", exception);
	}

	@Test
	void findSquadByPositionWithTooLarge() {
		final Event sut = buildEventWithSquads(Squad.builder().name("Squad").build());

		final BusinessRuntimeException exception = assertThrows(BusinessRuntimeException.class, () -> sut.findSquadByPosition(1));
		assertMessageEquals("Couldn't find a squad on position 1.", exception);
	}

	private Event buildEventWithSquads(Squad... squads) {
		return Event.builder()
				.squadList(Arrays.asList(squads))
				.build();
	}

	//isEmpty
	@Test
	void isEmptyReturnsTrue() {
		final Event sut = buildEventWithOneSlot(null);

		assertTrue(sut.isEmpty());
	}

	@Test
	void isEmptyDetectsSlottedUser() {
		final long DISCORD_USER = 123456789012345678L;
		assertTrue(MentionUtils.isSnowflake(Long.toString(DISCORD_USER)));

		final Event sut = buildEventWithOneSlot(DISCORD_USER);

		assertFalse(sut.isEmpty());
	}

	@Test
	void isEmptyDetectsBlockedSlot() {
		final Event sut = buildEventWithOneSlot(User.DEFAULT_USER_ID);

		assertFalse(sut.isEmpty());
	}

	private Event buildEventWithOneSlot(Long userId) {
		return Event.builder()
				.squadList(new ArrayList<>(
						List.of(Squad.builder().name("Squad")
								.slotList(new ArrayList<>(
										List.of(Slot.builder()
												.number(1)
												.user(userId != null ? User.builder().id(userId).build() : null)
												.build())))
								.build())))
				.build();
	}

	//validate
	@Test
	void validateAllowsMinimalEvent() {
		final Event sut = Event.builder()
				//Hibernate would initialize these as empty list
				.squadList(Collections.emptyList())
				.details(Collections.emptyList())
				.build();

		assertDoesNotThrow(sut::validate);
	}

	@Test
	void validateAllowsEvent() {
		final Event sut = Event.builder()
				.hidden(true)
				.shareable(true)
				.name("Event")
				.dateTime(LocalDateTime.now())
				.creator("Creator")
				.eventType(EventType.builder().build())
				.description("Description")
				.missionType("Mission Type")
				.missionLength("Mission Length")
				.pictureUrl("https://example.net")
				.details(List.of(EventField.builder().build()))
				.squadList(List.of(Squad.builder().slotList(List.of(Slot.builder().build())).build()))
				.reserveParticipating(true)
				.ownerGuild(Guild.builder().build())
				.build();

		assertDoesNotThrow(sut::validate);
	}

	@Test
	void validateDetectsDuplicatedSlotNumber() {
		final Event sut = Event.builder()
				.squadList(List.of(
						Squad.builder().slotList(List.of(Slot.builder().number(1).build())).build(),
						Squad.builder().slotList(List.of(Slot.builder().number(1).build())).build()
				))
				.build();

		final BusinessRuntimeException exception = assertThrows(BusinessRuntimeException.class, sut::validate);
		assertMessageEquals("Slotnummern müssen innerhalb eines Events eindeutig sein.", exception);
	}

	@Test
	void validateDetectsTooManyDetails() {
		final ArrayList<EventField> details = new ArrayList<>();
		for (int i = 0; i < 23; i++) {
			details.add(EventField.builder().build());
		}
		final Event sut = Event.builder()
				.squadList(Collections.emptyList())
				.details(details)
				.build();

		final BusinessRuntimeException exception = assertThrows(BusinessRuntimeException.class, sut::validate);
		assertMessageEquals("Es dürfen nur 22 Detailfelder angegeben werden.", exception);
	}

	//slotUpdate
	@Test
	void slotUpdateCreatesReserve() {
		Event sut = buildEventWithReserve(100, 101, 102);

		sut.slotUpdate();

		final Optional<Squad> reserve = sut.getSquadList().stream().filter(Squad::isReserve).findAny();
		assertThat(reserve).isPresent();
		final Squad reserveSquad = reserve.get();
		assertThat(reserveSquad.getSlotList()).hasSize(1);
		assertThat(getSlotNumbers(reserveSquad)).containsExactly(103);
	}

	@Test
	void slotUpdateCreatesUniqueSlotNumbers() {
		Event sut = buildEventWithReserve(100, 101, 102, 103, 104, 105, 106, 107);

		sut.slotUpdate();

		final Optional<Squad> reserve = sut.getSquadList().stream().filter(Squad::isReserve).findAny();
		assertThat(reserve).isPresent();
		final Squad reserveSquad = reserve.get();
		assertThat(reserveSquad.getSlotList()).hasSize(2);
		assertThat(getSlotNumbers(reserveSquad)).containsExactly(108, 109);
	}

	private Event buildEventWithReserve(int... slotNumbers) {
		User defaultUser = User.builder().id(User.DEFAULT_USER_ID).build();

		List<Slot> slotList = new ArrayList<>();
		for (int slotNumber : slotNumbers) {
			slotList.add(Slot.builder().number(slotNumber).user(defaultUser).build());
		}

		return Event.builder()
				.squadList(new ArrayList<>(List.of(Squad.builder().name("Squad")
						.slotList(slotList)
						.build())))
				.build();
	}

	private List<Integer> getSlotNumbers(@NonNull Squad squad) {
		return squad.getSlotList().stream().map(Slot::getNumber).toList();
	}

	//archive
	@Test
	void archiveUpdatesDiscordInformation() {
		final long archivingGuild = 1;
		final long otherGuild = 2;

		final EventDiscordInformation infoToKeep = EventDiscordInformation.builder().guild(Guild.builder().id(otherGuild).build()).build();
		final Event sut = Event.builder()
				.discordInformation(new HashSet<>(Arrays.asList(
						EventDiscordInformation.builder().guild(Guild.builder().id(archivingGuild).build()).build(),
						infoToKeep
				)))
				.ownerGuild(Guild.builder().build())
				.build();

		sut.archive(archivingGuild);

		assertThat(sut.getDiscordInformation())
				.hasSize(1)
				.containsExactly(infoToKeep);
	}

	@Test
	void archiveRemovesNotifications() {
		final int ownerGuild = 1;
		final Event sut = Event.builder()
				.ownerGuild(Guild.builder().id(ownerGuild).build())
				.discordInformation(Collections.emptySet())
				.build();

		try (MockedStatic<EventNotificationService> eventNotificationService = mockStatic(EventNotificationService.class)) {
			sut.archive(ownerGuild);

			eventNotificationService.verify(() -> EventNotificationService.removeNotifications(sut.getId()));
		}
	}

	@Test
	void archiveDoesntRemoveNotificationsForOtherGuild() {
		final int ownerGuild = 1;
		final int otherGuild = 2;
		final Event sut = Event.builder()
				.ownerGuild(Guild.builder().id(ownerGuild).build())
				.discordInformation(Collections.emptySet())
				.build();

		try (MockedStatic<EventNotificationService> eventNotificationService = mockStatic(EventNotificationService.class)) {
			sut.archive(otherGuild);

			eventNotificationService.verify(() -> EventNotificationService.removeNotifications(sut.getId()), never());
		}
	}
}