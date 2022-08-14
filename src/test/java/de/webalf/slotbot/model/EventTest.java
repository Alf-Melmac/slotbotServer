package de.webalf.slotbot.model;

import de.webalf.slotbot.util.bot.MentionUtils;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Alf
 * @since 20.10.2021
 */
class EventTest {
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

	//slotUpdate
	@Test
	void slotUpdateCreatesReserve() {
		Event sut = buildEventWithReserve(100, 101, 102);

		sut.slotUpdate();

		final Optional<Squad> reserve = sut.getSquadList().stream().filter(Squad::isReserve).findAny();
		assertTrue(reserve.isPresent());
		final Squad reserveSquad = reserve.get();
		assertThat(reserveSquad.getSlotList()).hasSize(1);
		assertThat(getSlotNumbers(reserveSquad)).containsExactly(103);
	}

	@Test
	void slotUpdateCreatesUniqueSlotNumbers() {
		Event sut = buildEventWithReserve(100, 101, 102, 103, 104, 105, 106, 107);

		sut.slotUpdate();

		final Optional<Squad> reserve = sut.getSquadList().stream().filter(Squad::isReserve).findAny();
		assertTrue(reserve.isPresent());
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
		return squad.getSlotList().stream().map(Slot::getNumber).collect(Collectors.toUnmodifiableList());
	}
}