package de.webalf.slotbot.model;

import de.webalf.slotbot.exception.ForbiddenException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.webalf.slotbot.AssertionUtils.assertMessageEquals;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Alf
 * @since 30.07.2021
 */
class SquadTest {
	//addSlot
	@Test
	void addSlotDoesNotAddToReserve() {
		final Squad reserveSquad = buildReserveSquad();
		final Slot newSlot = Slot.builder().squad(reserveSquad).build();

		final ForbiddenException exception = assertThrows(ForbiddenException.class, () -> reserveSquad.addSlot(newSlot));
		assertMessageEquals("Zur Reserve dürfen keine Slots hinzugefügt werden.", exception);
	}

	//deleteSlot
	@Test
	void deleteSlotDoesNotDeleteInReserve() {
		final Slot slotToDelete = Slot.builder().build();

		final Squad reserveSquad = buildReserveSquad();

		final ForbiddenException exception = assertThrows(ForbiddenException.class, () -> reserveSquad.deleteSlot(slotToDelete));
		assertMessageEquals("Ein Slot der Reserve kann nicht gelöscht werden.", exception);
	}

	@Test
	void deleteSlotDoesNotDeleteOccupiedSlot() {
		final Slot slotToDelete = Slot.builder().user(User.builder().build()).build();

		final Squad sut = Squad.builder().name("Squad").build();

		final ForbiddenException exception = assertThrows(ForbiddenException.class, () -> sut.deleteSlot(slotToDelete));
		assertMessageEquals("Der Slot ist belegt, die Person muss zuerst ausgeslottet werden.", exception);
	}

	@Test
	void deleteSlotCanDeleteBlockedSlot() {
		final Slot slotToDelete = Slot.builder().user(User.builder().id(User.DEFAULT_USER_ID).build()).build();

		final List<Slot> slotList = new ArrayList<>(Arrays.asList(
				Slot.builder().build(),
				slotToDelete
		));
		final Squad sut = Squad.builder()
				.name("Squad")
				.slotList(slotList)
				.event(Event.builder().squadList(Collections.emptyList()).build())
				.build();

		final List<Slot> expected = new ArrayList<>(slotList);
		expected.remove(slotToDelete);

		sut.deleteSlot(slotToDelete);

		assertIterableEquals(expected, sut.getSlotList());
	}

	@Test
	void deleteSlotDeletesSquadIfLastSlot() {
		final Slot slotToDelete = Slot.builder().user(User.builder().id(User.DEFAULT_USER_ID).build()).build();

		final Squad sut = Squad.builder()
				.name("Squad")
				.slotList(new ArrayList<>(Collections.singletonList(slotToDelete)))
				.event(Event.builder().squadList(Collections.emptyList()).build())
				.build();
		final Event event = sut.getEvent();
		event.setSquadList(new ArrayList<>(Collections.singletonList(sut)));

		sut.deleteSlot(slotToDelete);

		assertTrue(event.getSquadList().isEmpty());
	}

	public static Squad buildReserveSquad() {
		return Squad.builder().name(Squad.RESERVE_NAME).build();
	}
}