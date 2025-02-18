package de.webalf.slotbot.util;

import de.webalf.slotbot.feature.requirement.model.Requirement;
import de.webalf.slotbot.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static de.webalf.slotbot.util.MockUtils.TEST_LOCALE;
import static de.webalf.slotbot.util.MockUtils.mockMessage;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alf
 * @since 17.01.2022
 */
@ExtendWith(MockitoExtension.class)
class EventHelperSlotListTest {
	private static final String TITLE = "Title";

	@Mock
	MessageSource messageSource;

	@InjectMocks
	EventHelper sut;

	@BeforeEach
	void setUp() {
		mockMessage(messageSource, TITLE, "event.slotlist.title");
	}

	@Test
	void buildSlotListPrintsUser() {
		final Event event = Event.builder()
				.squadList(List.of(Squad.builder()
						.name("Squad")
						.slotList(List.of(Slot.builder()
								.number(1)
								.name("Slot")
								.requirements(Collections.emptySet())
								.user(User.builder().id(1234).build())
								.build()
						))
						.requirements(Collections.emptySet())
						.build()))
				.requirements(Collections.emptySet())
				.build();

		final List<String> slotList = sut.buildSlotList(event, 1L, TEST_LOCALE);
		assertThat(slotList).hasSize(1);
		assertThat(slotList.getFirst()).isEqualTo("__**" + TITLE + "**__\n\n**Squad**\n1 Slot: <@1234>");
	}

	@Test
	void buildSlotListPrintsBlockedSlot() {
		final Event event = Event.builder()
				.squadList(List.of(Squad.builder()
						.name("Squad")
						.slotList(List.of(Slot.builder()
								.number(1)
								.name("Slot")
								.requirements(Collections.emptySet())
								.user(User.builder().id(User.DEFAULT_USER_ID).build())
								.replacementText("Replacement")
								.build()
						))
						.requirements(Collections.emptySet())
						.build()))
				.requirements(Collections.emptySet())
				.build();

		final List<String> slotList = sut.buildSlotList(event, 1L, TEST_LOCALE);
		assertThat(slotList).hasSize(1);
		assertThat(slotList.getFirst()).isEqualTo("__**" + TITLE + "**__\n\n**Squad**\n1 Slot: *Replacement*");
	}

	@Test
	void buildSlotListMarksReservationsForOneGuild() {
		final long g1 = 1L;
		final Event event = buildReservedEvent(g1, 2);

		final List<String> slotList = sut.buildSlotList(event, g1, TEST_LOCALE);
		assertThat(slotList).hasSize(1);
		assertThat(slotList.getFirst()).isEqualTo("__**" + TITLE + "**__\n\n**Squad free**\n**1 Slot free**:\n**2 Slot g1** [g1]:\n3 Slot g2 [g2]:\n\n**Squad g1** [g1]\n**4 Slot free** [g1]:\n**5 Slot g1** [g1]:\n6 Slot g2 [g2]:\n\n**Squad g2** [g2]\n7 Slot free [g2]:\n**8 Slot g1** [g1]:\n9 Slot g2 [g2]:");
	}

	@Test
	void buildSlotListMarksReservationsForOtherGuild() {
		final long g2 = 2L;
		final Event event = buildReservedEvent(1, g2);

		final List<String> slotList = sut.buildSlotList(event, g2, TEST_LOCALE);
		assertThat(slotList).hasSize(1);
		assertThat(slotList.getFirst()).isEqualTo("__**" + TITLE + "**__\n\n**Squad free**\n**1 Slot free**:\n2 Slot g1 [g1]:\n**3 Slot g2** [g2]:\n\n**Squad g1** [g1]\n4 Slot free [g1]:\n5 Slot g1 [g1]:\n**6 Slot g2** [g2]:\n\n**Squad g2** [g2]\n**7 Slot free** [g2]:\n8 Slot g1 [g1]:\n**9 Slot g2** [g2]:");
	}

	@Test
	void buildSlotListMarksReservationsForForeignGuild() {
		final Event event = buildReservedEvent(1, 2);

		final List<String> slotList = sut.buildSlotList(event, 3L, TEST_LOCALE);
		assertThat(slotList).hasSize(1);
		assertThat(slotList.getFirst()).isEqualTo("__**" + TITLE + "**__\n\n**Squad free**\n**1 Slot free**:\n2 Slot g1 [g1]:\n3 Slot g2 [g2]:\n\n**Squad g1** [g1]\n4 Slot free [g1]:\n5 Slot g1 [g1]:\n6 Slot g2 [g2]:\n\n**Squad g2** [g2]\n7 Slot free [g2]:\n8 Slot g1 [g1]:\n9 Slot g2 [g2]:");
	}

	private Event buildReservedEvent(long guild1, long guild2) {
		final Guild g1 = Guild.builder().id(guild1).groupIdentifier("g1").build();
		final Guild g2 = Guild.builder().id(guild2).groupIdentifier("g2").build();

		final Event event = Event.builder()
				.squadList(List.of(
						Squad.builder()
								.name("Squad free")
								.slotList(List.of(
										Slot.builder()
												.number(1)
												.name("Slot free")
												.requirements(Collections.emptySet())
												.build(),
										Slot.builder()
												.number(2)
												.name("Slot g1")
												.reservedFor(g1)
												.requirements(Collections.emptySet())
												.build(),
										Slot.builder()
												.number(3)
												.name("Slot g2")
												.reservedFor(g2)
												.requirements(Collections.emptySet())
												.build()
								))
								.requirements(Collections.emptySet())
								.build(),
						Squad.builder()
								.name("Squad g1")
								.reservedFor(g1)
								.slotList(List.of(
										Slot.builder()
												.name("Slot free")
												.number(4)
												.requirements(Collections.emptySet())
												.build(),
										Slot.builder()
												.number(5)
												.name("Slot g1")
												.reservedFor(g1)
												.requirements(Collections.emptySet())
												.build(),
										Slot.builder()
												.number(6)
												.name("Slot g2")
												.reservedFor(g2)
												.requirements(Collections.emptySet())
												.build()
								))
								.requirements(Collections.emptySet())
								.build(),
						Squad.builder()
								.name("Squad g2")
								.reservedFor(g2)
								.slotList(List.of(
										Slot.builder()
												.number(7)
												.name("Slot free")
												.requirements(Collections.emptySet())
												.build(),
										Slot.builder()
												.number(8)
												.name("Slot g1")
												.reservedFor(g1)
												.requirements(Collections.emptySet())
												.build(),
										Slot.builder()
												.number(9)
												.name("Slot g2")
												.reservedFor(g2)
												.requirements(Collections.emptySet())
												.build()
								))
								.requirements(Collections.emptySet())
								.build()))
				.details(Collections.emptyList())
				.requirements(Collections.emptySet())
				.build();
		event.setBackReferences();
		return event;
	}

	@Test
	void buildSlotListMarksRequirements() {
		final Event event = Event.builder()
				.requirements(Set.of(Requirement.builder().name("Event Requirement").abbreviation("er").build()))
				.squadList(List.of(Squad.builder()
						.name("Squad")
						.requirements(Set.of(Requirement.builder().name("Squad Requirement").abbreviation("sr").build()))
						.slotList(List.of(Slot.builder()
								.number(1)
								.name("Slot")
								.requirements(Set.of(Requirement.builder().name("Slot Requirement").abbreviation("slr").build()))
								.build()))
						.build()))
				.build();

		final List<String> slotList = sut.buildSlotList(event, 1L, TEST_LOCALE);
		assertThat(slotList).hasSize(1);
		assertThat(slotList.getFirst()).isEqualTo("__**" + TITLE + "**__ [er]\n\n**Squad** [sr]\n**1 Slot** [slr]:");
	}
}