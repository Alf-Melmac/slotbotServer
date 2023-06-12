package de.webalf.slotbot.model;

import de.webalf.slotbot.exception.BusinessRuntimeException;
import org.junit.jupiter.api.Test;

import java.util.*;

import static de.webalf.slotbot.AssertionUtils.assertMessageEquals;
import static de.webalf.slotbot.model.SquadTest.buildReserveSquad;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Alf
 * @since 30.07.2021
 */
class SlotTest {
	//isSlotWithSlottedUser
	@Test
	void slotWithUserReturnsTrue() {
		final User slottedUser = User.builder().build();

		final Slot sut = Slot.builder().user(slottedUser).build();

		assertTrue(sut.isSlotWithSlottedUser(slottedUser));
	}

	@Test
	void slotWithUserReturnsFalse() {
		final User slottedUser = User.builder().id(123).build();
		final User otherUser = User.builder().id(456).build();

		final Slot sut = Slot.builder().user(slottedUser).build();

		assertFalse(sut.isSlotWithSlottedUser(otherUser));
	}

	@Test
	void slotWithUserChecksEmptySlot() {
		final Slot sut = Slot.builder().build();

		assertFalse(sut.isSlotWithSlottedUser(User.builder().build()));
	}

	//isBlocked
	@Test
	void isBlockedDetectsEmptySlot() {
		final Slot sut = Slot.builder().build();

		assertFalse(sut.isBlocked());
	}

	@Test
	void isBlockedDetectsOccupiedSlot() {
		final Slot sut = Slot.builder().user(User.builder().build()).build();

		assertFalse(sut.isBlocked());
	}

	@Test
	void isBlockedDetectsBlockedSlot() {
		final Slot sut = buildBlockedSlot();

		assertTrue(sut.isBlocked());
	}

	private static Slot buildBlockedSlot() {
		return buildBlockedSlot(null);
	}

	private static Slot buildBlockedSlot(String replacementText) {
		return Slot.builder()
				.user(User.builder().id(User.DEFAULT_USER_ID).build())
				.replacementText(replacementText)
				.build();
	}

	//getEffectiveReservedForDisplay
	@Test
	void reservedForDisplayWithNoReservation() {
		final Slot sut = Slot.builder().squad(Squad.builder().build()).build();

		assertNull(sut.getEffectiveReservedForDisplay());
	}

	@Test
	void reservedForDisplayWithSlotReserved() {
		final Guild guild = Guild.builder().build();
		final Slot sut = Slot.builder().reservedFor(guild).squad(Squad.builder().build()).build();

		assertEquals(guild, sut.getEffectiveReservedForDisplay());
	}

	@Test
	void reservedForDisplayWithSquadReservedOptimized() {
		final Guild guild = Guild.builder().build();
		final Slot sut = prepareSlot(Slot.builder().build(), Squad.builder().reservedFor(guild).build());

		assertNull(sut.getEffectiveReservedForDisplay());
	}

	@Test
	void reservedForDisplayWithSquadReservedNotOptimized() {
		final Guild oneGuild = Guild.builder().id(1L).build();
		final Guild otherGuild = Guild.builder().id(2L).build();
		final Slot sut = prepareSlot(
				Slot.builder().build(),
				Squad.builder().reservedFor(oneGuild).build(),
				Slot.builder().reservedFor(otherGuild).build());

		assertEquals(oneGuild, sut.getEffectiveReservedForDisplay());
	}

	@Test
	void reservedForDisplayWithSlotReservedAndSquadWithOtherReserved() {
		final Guild oneGuild = Guild.builder().id(1L).build();
		final Guild otherGuild = Guild.builder().id(2L).build();
		final Slot sut = prepareSlot(Slot.builder().reservedFor(oneGuild).build(), Squad.builder().reservedFor(otherGuild).build());

		assertEquals(oneGuild, sut.getEffectiveReservedForDisplay());
	}

	@Test
	void reservedForDisplayWithSlotAndSquadReservedOptimized() {
		final Guild oneGuild = Guild.builder().build();
		final Slot sut = prepareSlot(Slot.builder().reservedFor(oneGuild).build(), Squad.builder().reservedFor(oneGuild).build());

		assertNull(sut.getEffectiveReservedForDisplay());
	}

	@Test
	void reservedForDisplayWithSlotAndSquadReservedNotOptimized() {
		final Guild oneGuild = Guild.builder().id(1L).build();
		final Guild otherGuild = Guild.builder().id(2L).build();
		final Slot sut = prepareSlot(
				Slot.builder().reservedFor(oneGuild).build(),
				Squad.builder().reservedFor(oneGuild).build(),
				Slot.builder().reservedFor(otherGuild).build());

		assertEquals(oneGuild, sut.getEffectiveReservedForDisplay());
	}

	private Slot prepareSlot(Slot slot, Squad squad, Slot... otherSlots) {
		List<Slot> slots;
		if (otherSlots.length > 0) {
			slots = new ArrayList<>(Arrays.asList(otherSlots));
			slots.add(slot);
		} else {
			slots = List.of(slot);
		}
		squad.setSlotList(slots);
		slot.setSquad(squad);
		return slot;
	}

	//getReplacementTextOrDefault
	@Test
	void getReplacementTextOrDefaultWithNotBlockedSlot() {
		final Slot sut = Slot.builder().build();

		assertNull(sut.getReplacementTextOrDefault());
	}

	@Test
	void getReplacementTextOrDefaultWithoutReplacementText() {
		final Slot sut = buildBlockedSlot();

		assertEquals("Gesperrt", sut.getReplacementTextOrDefault());
	}

	@Test
	void getReplacementTextOrDefaultWithReplacementText() {
		final String replacementText = "replacementText";
		final Slot sut = buildBlockedSlot(replacementText);

		assertEquals(replacementText, sut.getReplacementTextOrDefault());
	}

	//slotWithoutUpdate
	@Test
	void slotPreventsSlottingOnSameSlot() {
		final User userToSlot = User.builder().build();

		final Slot sut = Slot.builder().user(userToSlot).build();

		final BusinessRuntimeException exception = assertThrows(BusinessRuntimeException.class, () -> sut.slotWithoutUpdate(userToSlot));
		assertMessageEquals("Die Person ist bereits auf diesem Slot", exception);
	}

	@Test
	void slotSlotsOnEmptySlot() {
		final User userToSlot = User.builder().build();

		final Slot sut = Slot.builder().squad(Squad.builder().event(Event.builder().squadList(Collections.emptyList()).build()).build()).build();

		assertNull(sut.getUser());
		sut.slotWithoutUpdate(userToSlot);
		assertEquals(userToSlot, sut.getUser());
	}

	@Test
	void slotSwitchesSlot() {
		final User userToSlot = User.builder().build();

		final Slot slot1 = Slot.builder().number(1).user(userToSlot).build();
		final Slot sut = Slot.builder().number(2).build();

		Event.builder()
				.squadList(List.of(Squad.builder().name("Squad").slotList(List.of(slot1, sut)).build()))
				.details(Collections.emptyList())
				.build()
				.setBackReferences();

		sut.slotWithoutUpdate(userToSlot);

		assertNull(slot1.getUser()); //Removed from old slot
		assertEquals(userToSlot, sut.getUser()); //Slotted in new slot
	}

	@Test
	void slotDoesNotOverrideOccupiedSlot() {
		final User userToSlot = User.builder().id(1234).build();
		final User otherUser = User.builder().id(5678).build();

		final Slot sut = Slot.builder().user(otherUser).build();

		final BusinessRuntimeException exception = assertThrows(BusinessRuntimeException.class, () -> sut.slotWithoutUpdate(userToSlot));
		assertMessageEquals("Auf dem Slot befindet sich eine andere Person", exception);
	}

	@Test
	void slotAllowsSlottingOnReservedSlot() {
		final User userToSlot = User.builder().build();
		final Guild guild = Guild.builder().id(1L).build();
		userToSlot.setGuilds(Set.of(GuildUsers.builder().guild(guild).user(userToSlot).build()));

		final Slot sut = Slot.builder()
				.reservedFor(guild)
				.squad(Squad.builder()
						.event(Event.builder().squadList(Collections.emptyList()).build())
						.build())
				.build();

		assertNull(sut.getUser());
		sut.slotWithoutUpdate(userToSlot);
		assertEquals(userToSlot, sut.getUser());
	}

	@Test
	void slotAllowsSlottingInReservedSquad() {
		final User userToSlot = User.builder().build();
		final Guild guild = Guild.builder().id(1L).build();
		userToSlot.setGuilds(Set.of(GuildUsers.builder().guild(guild).user(userToSlot).build()));

		final Slot sut = Slot.builder()
				.squad(Squad.builder()
						.reservedFor(guild)
						.event(Event.builder().squadList(Collections.emptyList()).build())
						.build())
				.build();

		assertNull(sut.getUser());
		sut.slotWithoutUpdate(userToSlot);
		assertEquals(userToSlot, sut.getUser());
	}

	@Test
	void slotPreventsSlottingOnReservedSlot() {
		final User userToSlot = User.builder().build();

		final Slot sut = Slot.builder().reservedFor(Guild.builder().build()).build();

		final BusinessRuntimeException exception = assertThrows(BusinessRuntimeException.class, () -> sut.slotWithoutUpdate(userToSlot));
		assertMessageEquals("Dieser Slot ist für Mitglieder einer anderen Gruppe reserviert", exception);
	}

	@Test
	void slotPreventsSlottingInReservedSquad() {
		final User userToSlot = User.builder().build();

		final Slot sut = Slot.builder().squad(Squad.builder().reservedFor(Guild.builder().build()).build()).build();

		final BusinessRuntimeException exception = assertThrows(BusinessRuntimeException.class, () -> sut.slotWithoutUpdate(userToSlot));
		assertMessageEquals("Dieser Slot ist für Mitglieder einer anderen Gruppe reserviert", exception);
	}

	//unslotWithoutUpdate
	@Test
	void unslotUnslots() {
		final User userToUnslot = User.builder().build();

		final Slot sut = Slot.builder().user(userToUnslot).build();

		assertEquals(userToUnslot, sut.getUser());
		sut.unslotWithoutUpdate(userToUnslot);
		assertNull(sut.getUser());
	}

	@Test
	void unslotIgnoresEmptySlot() {
		final Slot sut = Slot.builder().build();

		assertNull(sut.getUser());
		sut.unslotWithoutUpdate(null);
		assertNull(sut.getUser());
	}

	@Test
	void unslotDoesNotUnslotOtherUser() {
		final User userToUnslot = User.builder().id(1234).build();
		final User otherUser = User.builder().id(5678).build();

		final Slot sut = Slot.builder().user(otherUser).build();

		assertEquals(otherUser, sut.getUser());
		final BusinessRuntimeException exception = assertThrows(BusinessRuntimeException.class, () -> sut.unslotWithoutUpdate(userToUnslot));
		assertMessageEquals("Auf dem Slot befindet sich eine andere Person", exception);
	}

	//blockSlot
	@Test
	void blockSlotDoesNotBlockOccupiedSlot() {
		final Slot sut = Slot.builder().user(User.builder().build()).build();

		final User defaultUser = User.builder().build();
		final BusinessRuntimeException exception = assertThrows(BusinessRuntimeException.class, () ->
				sut.blockSlot(defaultUser, ""));
		assertMessageEquals("Der Slot ist belegt, die Person muss zuerst ausgeslottet werden.", exception);
	}

	@Test
	void blockSlotDoesNotBlockInReserve() {
		final Slot sut = Slot.builder().squad(buildReserveSquad()).build();

		final User defaultUser = User.builder().build();
		final BusinessRuntimeException exception = assertThrows(BusinessRuntimeException.class, () ->
				sut.blockSlot(defaultUser, ""));
		assertMessageEquals("In der Reserve kann kein Slot blockiert werden.", exception);
	}

	@Test
	void blockSlotSetsDefaultUser() {
		final User defaultUser = User.builder().build();

		final Slot sut = Slot.builder()
				.squad(Squad.builder().name("Squad")
						.event(Event.builder()
								.squadList(Collections.emptyList())
								.build())
						.build())
				.build();
		sut.blockSlot(defaultUser, "");

		assertEquals(defaultUser, sut.getUser());
	}

	@Test
	void blockSlotSetsReplacementText() {
		final User defaultUser = User.builder().build();
		final String replacement = "Replacement";

		final Slot sut = Slot.builder()
				.squad(Squad.builder().name("Squad")
						.event(Event.builder()
								.squadList(Collections.emptyList())
								.build())
						.build())
				.build();
		sut.blockSlot(defaultUser, replacement);

		assertEquals(replacement, sut.getReplacementText());
	}

	//swapUsers
	@Test
	void swapUsersSwaps() {
		final User currentUser = User.builder().id(1234).build();
		final User swappedUser = User.builder().id(5678).build();

		final Slot currentSlot = Slot.builder().number(1).user(currentUser).build();
		final Slot swapSlot = Slot.builder().number(2).user(swappedUser).build();

		assertEquals(currentUser, currentSlot.getUser());
		assertEquals(swappedUser, swapSlot.getUser());
		currentSlot.swapUsers(swapSlot);
		assertEquals(swappedUser, currentSlot.getUser());
		assertEquals(currentUser, swapSlot.getUser());
	}
}