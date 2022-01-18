package de.webalf.slotbot.model.dtos.referenceless;

import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.GuildDto;
import de.webalf.slotbot.model.dtos.UserDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alf
 * @since 17.01.2022
 */
class EventReferencelessDtoTest {
	@Test
	void getSlotListPrintsUser() {
		final EventReferencelessDto sut = EventReferencelessDto.builder()
				.squadList(List.of(SquadReferencelessDto.builder()
						.name("Squad")
						.slotList(List.of(SlotReferencelessDto.builder()
								.number(1)
								.name("Slot")
								.user(UserDto.builder().id("1234").build())
								.build()
						))
						.build()))
				.build();

		final List<String> slotList = sut.getSlotList(1L);
		assertThat(slotList.size()).isEqualTo(1);
		assertThat(slotList.get(0)).isEqualTo("__**Teilnahmeplatzaufzählung**__\n\n**Squad**\n1 Slot: <@1234>");
	}

	@Test
	void getSlotListPrintsBlockedSlot() {
		final EventReferencelessDto sut = EventReferencelessDto.builder()
				.squadList(List.of(SquadReferencelessDto.builder()
						.name("Squad")
						.slotList(List.of(SlotReferencelessDto.builder()
								.number(1)
								.name("Slot")
								.user(UserDto.builder().id(Long.toString(User.DEFAULT_USER_ID)).build())
								.build()
						))
						.build()))
				.build();

		final List<String> slotList = sut.getSlotList(1L);
		assertThat(slotList.size()).isEqualTo(1);
		assertThat(slotList.get(0)).isEqualTo("__**Teilnahmeplatzaufzählung**__\n\n**Squad**\n1 Slot: <@1234>");
	}

	@Test
	void getSlotListMarksReservationsForOneGuild() {
		final long g1 = 1L;
		final EventReferencelessDto sut = buildEvent(g1, 2L);

		final List<String> slotList = sut.getSlotList(g1);
		assertThat(slotList.size()).isEqualTo(1);
		assertThat(slotList.get(0)).isEqualTo("__**Teilnahmeplatzaufzählung**__\n\n**Squad free**\n**1 Slot free**:\n**2 Slot g1**:\n3 Slot g2:\n\n**Squad g1**\n**4 Slot free**:\n**5 Slot g1**:\n6 Slot g2:\n\n**Squad g2**\n7 Slot free:\n**8 Slot g1**:\n9 Slot g2:");
	}

	@Test
	void getSlotListMarksReservationsForOtherGuild() {
		final long g2 = 2L;
		final EventReferencelessDto sut = buildEvent(1L, g2);

		final List<String> slotList = sut.getSlotList(g2);
		assertThat(slotList.size()).isEqualTo(1);
		assertThat(slotList.get(0)).isEqualTo("__**Teilnahmeplatzaufzählung**__\n\n**Squad free**\n**1 Slot free**:\n2 Slot g1:\n**3 Slot g2**:\n\n**Squad g1**\n4 Slot free:\n5 Slot g1:\n**6 Slot g2**:\n\n**Squad g2**\n**7 Slot free**:\n8 Slot g1:\n**9 Slot g2**:");
	}

	@Test
	void getSlotListMarksReservationsForForeignGuild() {
		final EventReferencelessDto sut = buildEvent(1L, 2L);

		final List<String> slotList = sut.getSlotList(3L);
		assertThat(slotList.size()).isEqualTo(1);
		assertThat(slotList.get(0)).isEqualTo("__**Teilnahmeplatzaufzählung**__\n\n**Squad free**\n**1 Slot free**:\n2 Slot g1:\n3 Slot g2:\n\n**Squad g1**\n4 Slot free:\n5 Slot g1:\n6 Slot g2:\n\n**Squad g2**\n7 Slot free:\n8 Slot g1:\n9 Slot g2:");
	}

	private EventReferencelessDto buildEvent(long guild1, long guild2) {
		final GuildDto g1 = GuildDto.builder().id(guild1).build();
		final GuildDto g2 = GuildDto.builder().id(guild2).build();

		return EventReferencelessDto.builder()
				.squadList(List.of(
						SquadReferencelessDto.builder()
								.name("Squad free")
								.slotList(List.of(
										SlotReferencelessDto.builder()
												.number(1)
												.name("Slot free")
												.build(),
										SlotReferencelessDto.builder()
												.number(2)
												.name("Slot g1")
												.reservedFor(g1)
												.build(),
										SlotReferencelessDto.builder()
												.number(3)
												.name("Slot g2")
												.reservedFor(g2)
												.build()
								)).build(),
						SquadReferencelessDto.builder()
								.name("Squad g1")
								.reservedFor(g1)
								.slotList(List.of(
										SlotReferencelessDto.builder()
												.name("Slot free")
												.number(4)
												.build(),
										SlotReferencelessDto.builder()
												.number(5)
												.name("Slot g1")
												.reservedFor(g1)
												.build(),
										SlotReferencelessDto.builder()
												.number(6)
												.name("Slot g2")
												.reservedFor(g2)
												.build()
								)).build(),
						SquadReferencelessDto.builder()
								.name("Squad g2")
								.reservedFor(g2)
								.slotList(List.of(
										SlotReferencelessDto.builder()
												.number(7)
												.name("Slot free")
												.build(),
										SlotReferencelessDto.builder()
												.number(8)
												.name("Slot g1")
												.reservedFor(g1)
												.build(),
										SlotReferencelessDto.builder()
												.number(9)
												.name("Slot g2")
												.reservedFor(g2)
												.build()
								)).build()))
				.build();
	}
}