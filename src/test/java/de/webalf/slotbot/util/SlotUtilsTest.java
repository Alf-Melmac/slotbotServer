package de.webalf.slotbot.util;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.Squad;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alf
 * @since 10.04.2023
 */
class SlotUtilsTest {
	@ParameterizedTest(name = "{0}")
	@MethodSource
	void getEffectiveReservedForDisplay(String name, Guild reservedFor, Squad squad, Guild expected) {
		assertEquals(expected, SlotUtils.getEffectiveReservedForDisplay(reservedFor, squad));
	}

	private static Stream<Arguments> getEffectiveReservedForDisplay() {
		final Guild guild1 = Guild.builder().id(1L).build();
		final Guild guild2 = Guild.builder().id(2L).build();

		final Squad squadAndAllSlots = Squad.builder().reservedFor(guild1).slotList(List.of(buildSlot(guild1), buildSlot(guild1))).build();
		final Squad squadAndDifferentSlots = Squad.builder().reservedFor(guild1).slotList(List.of(buildSlot(guild1), buildSlot(guild2))).build();

		return Stream.of(
				Arguments.of("Slot and complete Squad reserved for the same guild", guild1, squadAndAllSlots, null),
				Arguments.of("Slot and Squad partial reserved", guild1, squadAndDifferentSlots, guild1),
				Arguments.of("Slot and Squad differently reserved", guild1, Squad.builder().reservedFor(guild2).build(), guild1),
				Arguments.of("Slot reserved, Squad not", guild1, Squad.builder().build(), guild1),
				Arguments.of("Slot not reserved and Squad completely", null, squadAndAllSlots, null),
				Arguments.of("Slot not reserved and Squad partial", null, squadAndDifferentSlots, squadAndDifferentSlots.getReservedFor()),
				Arguments.of("Slot and Squad not reserved", null, Squad.builder().build(), null)
		);
	}

	private static Slot buildSlot(Guild reservedFor) {
		return Slot.builder().reservedFor(reservedFor).build();
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource
	void getEffectiveReservedForDisplayWithDto(String name, Guild reservedFor, Guild squadReservedFor, List<Slot> slotList, Guild expected) {
		assertEquals(expected, SlotUtils.getEffectiveReservedForDisplay(reservedFor, squadReservedFor, slotList));
	}

	private static Stream<Arguments> getEffectiveReservedForDisplayWithDto() {
		final Guild guild1 = Guild.builder().id(1).build();
		final Guild guild2 = Guild.builder().id(2).build();

		final List<Slot> allSlots = List.of(buildSlot(guild1), buildSlot(guild1));
		final List<Slot> differentSlots = List.of(buildSlot(guild1), buildSlot(guild2));

		return Stream.of(
				Arguments.of("Slot and complete Squad reserved for the same guild", guild1, guild1, allSlots, null),
				Arguments.of("Slot and Squad partial reserved", guild1, guild1, differentSlots, guild1),
				Arguments.of("Slot and Squad differently reserved", guild1, guild2, null, guild1),
				Arguments.of("Slot reserved, Squad not", guild1, null, null, guild1),
				Arguments.of("Slot not reserved and Squad completely", null, guild1, allSlots, null),
				Arguments.of("Slot not reserved and Squad partial", null, guild1, differentSlots, guild1),
				Arguments.of("Slot and Squad not reserved", null, null, null, null)
		);
	}
}
