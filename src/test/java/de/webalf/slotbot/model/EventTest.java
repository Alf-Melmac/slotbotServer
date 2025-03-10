package de.webalf.slotbot.model;

import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.feature.notifications.EventNotificationService;
import de.webalf.slotbot.util.bot.MentionUtils;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.util.*;

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
		for (int i = 0; i < 24; i++) {
			details.add(EventField.builder().build());
		}
		final Event sut = Event.builder()
				.squadList(Collections.emptyList())
				.details(details)
				.build();

		final BusinessRuntimeException exception = assertThrows(BusinessRuntimeException.class, sut::validate);
		assertMessageEquals("Es dürfen nur 23 Detailfelder angegeben werden.", exception);
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