package de.webalf.slotbot.model;

import de.webalf.slotbot.exception.BusinessRuntimeException;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static de.webalf.slotbot.AssertionUtils.assertMessageEquals;
import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Alf
 * @since 30.07.2021
 */
class SlotTest {
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
				.setChilds();

		sut.slot(userToSlot);

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
		final Slot sut = Slot.builder().squad(Squad.builder().name(Squad.RESERVE_NAME).build()).build();

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