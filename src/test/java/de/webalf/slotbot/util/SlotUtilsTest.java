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
		final Guild GUILD = Guild.builder().id(1L).build();
		final Guild GUILD_2 = Guild.builder().id(2L).build();

		final Squad SQUAD_AND_ALL_SLOTS = Squad.builder().reservedFor(GUILD).slotList(List.of(buildSlot(GUILD), buildSlot(GUILD))).build();
		final Squad SQUAD_AND_DIFFERENT_SLOTS = Squad.builder().reservedFor(GUILD).slotList(List.of(buildSlot(GUILD), buildSlot(GUILD_2))).build();

		return Stream.of(
				Arguments.of("Slot and complete Squad reserved for the same guild", GUILD, SQUAD_AND_ALL_SLOTS, null),
				Arguments.of("Slot and Squad partial reserved", GUILD, SQUAD_AND_DIFFERENT_SLOTS, GUILD),
				Arguments.of("Slot and Squad differently reserved", GUILD, Squad.builder().reservedFor(GUILD_2).build(), GUILD),
				Arguments.of("Slot reserved, Squad not", GUILD, Squad.builder().build(), GUILD),
				Arguments.of("Slot not reserved and Squad completely", null, SQUAD_AND_ALL_SLOTS, null),
				Arguments.of("Slot not reserved and Squad partial", null, SQUAD_AND_DIFFERENT_SLOTS, SQUAD_AND_DIFFERENT_SLOTS.getReservedFor()),
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
		final Guild GUILD = Guild.builder().id(1).build();
		final Guild GUILD_2 = Guild.builder().id(2).build();

		final List<Slot> ALL_SLOTS = List.of(buildSlot(GUILD), buildSlot(GUILD));
		final List<Slot> DIFFERENT_SLOTS = List.of(buildSlot(GUILD), buildSlot(GUILD_2));

		return Stream.of(
				Arguments.of("Slot and complete Squad reserved for the same guild", GUILD, GUILD, ALL_SLOTS, null),
				Arguments.of("Slot and Squad partial reserved", GUILD, GUILD, DIFFERENT_SLOTS, GUILD),
				Arguments.of("Slot and Squad differently reserved", GUILD, GUILD_2, null, GUILD),
				Arguments.of("Slot reserved, Squad not", GUILD, null, null, GUILD),
				Arguments.of("Slot not reserved and Squad completely", null, GUILD, ALL_SLOTS, null),
				Arguments.of("Slot not reserved and Squad partial", null, GUILD, DIFFERENT_SLOTS, GUILD),
				Arguments.of("Slot and Squad not reserved", null, null, null, null)
		);
	}
}
