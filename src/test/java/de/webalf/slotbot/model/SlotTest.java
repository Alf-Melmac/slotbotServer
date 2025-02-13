package de.webalf.slotbot.model;

import de.webalf.slotbot.exception.BusinessRuntimeException;
import de.webalf.slotbot.feature.requirement.model.Requirement;
import de.webalf.slotbot.feature.requirement.model.RequirementList;
import de.webalf.slotbot.feature.slot_rules.Slottable;
import de.webalf.slotbot.model.enums.SlottableState;
import org.junit.jupiter.api.Test;

import java.util.*;

import static de.webalf.slotbot.AssertionUtils.assertMessageEquals;
import static de.webalf.slotbot.model.SquadTest.buildReserveSquad;
import static de.webalf.slotbot.model.enums.SlottableState.*;
import static org.assertj.core.api.Assertions.assertThat;
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

	private static Slot prepareSlot(Slot slot, Squad squad, Slot... otherSlots) {
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

	//getEffectiveRequirements
	@Test
	void getEffectiveRequirementsWithNoRequirement() {
		final Slot sut = buildEmptySlot();

		assertThat(sut.getEffectiveRequirements()).isEmpty();
	}

	@Test
	void getEffectiveRequirementsWithSlotRequirement() {
		final Requirement requirement = Requirement.builder().build();
		final Slot sut = buildSlotWithRequirements(Set.of(requirement));

		assertThat(sut.getEffectiveRequirements())
				.containsExactlyInAnyOrder(requirement);
	}

	@Test
	void getEffectiveRequirementsWithSquadRequirement() {
		final Requirement requirement = Requirement.builder().build();
		final Slot sut = buildSlotWithRequirements(Collections.emptySet(), Set.of(requirement), Collections.emptySet());

		assertThat(sut.getEffectiveRequirements())
				.containsExactlyInAnyOrder(requirement);
	}

	@Test
	void getEffectiveRequirementsWithEventRequirement() {
		final Requirement requirement = Requirement.builder().build();
		final Slot sut = buildSlotWithRequirements(Collections.emptySet(), Collections.emptySet(), Set.of(requirement));

		assertThat(sut.getEffectiveRequirements())
				.containsExactlyInAnyOrder(requirement);
	}

	@Test
	void getEffectiveRequirementsWithAllRequirements() {
		final Requirement slotRequirement = Requirement.builder().id(1).build();
		final Requirement squadRequirement = Requirement.builder().id(2).build();
		final Requirement eventRequirement = Requirement.builder().id(3).build();
		final Slot sut = buildSlotWithRequirements(Set.of(slotRequirement), Set.of(squadRequirement), Set.of(eventRequirement));

		assertThat(sut.getEffectiveRequirements())
				.containsExactlyInAnyOrder(slotRequirement, squadRequirement, eventRequirement);
	}

	private static Slot buildSlotWithRequirements(Set<Requirement> slotRequirements) {
		return buildSlotWithRequirements(slotRequirements, Collections.emptySet(), Collections.emptySet());
	}

	private static Slot buildSlotWithRequirements(Set<Requirement> slotRequirements, Set<Requirement> squadRequirements, Set<Requirement> eventRequirements) {
		return Slot.builder()
				.squad(Squad.builder()
						.event(Event.builder()
								.requirements(eventRequirements)
								.build())
						.requirements(squadRequirements)
						.build())
				.requirements(slotRequirements)
				.build();
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

	//getSlottable
	@Test
	void getSlottableWithBlocked() {
		final User userToSlot = User.builder().build();
		final Slot sut = buildBlockedSlot();

		assertThat(sut.getSlottable(userToSlot))
				.extracting(Slottable::state)
				.isEqualTo(NO_BLOCKED)
				.extracting(SlottableState::isSlottingAllowed)
				.isEqualTo(false);
	}

	@Test
	void slotIsPossibleWithOwnSlot() {
		final User userToSlot = User.builder().build();
		final Slot sut = Slot.builder().user(userToSlot).build();

		assertThat(sut.getSlottable(userToSlot))
				.extracting(Slottable::state)
				.isEqualTo(YES_OWN)
				.extracting(SlottableState::isSlottingAllowed)
				.isEqualTo(true);
	}

	@Test
	void slotIsPossibleWithOccupiedSlot() {
		final User userToSlot = User.builder().id(1234).build();
		final User otherUser = User.builder().id(5678).build();
		final Slot sut = Slot.builder().user(otherUser).build();

		assertThat(sut.getSlottable(userToSlot))
				.extracting(Slottable::state)
				.isEqualTo(NO)
				.extracting(SlottableState::isSlottingAllowed)
				.isEqualTo(false);
	}

	@Test
	void slotIsPossibleWithReservedForOwnSlot() {
		final User userToSlot = User.builder().build();
		final Guild guild = userInGuild(userToSlot);
		final Slot sut = buildEmptySlot();
		sut.setReservedFor(guild);

		assertThat(sut.getSlottable(userToSlot))
				.extracting(Slottable::state)
				.isEqualTo(YES)
				.extracting(SlottableState::isSlottingAllowed)
				.isEqualTo(true);
	}

	@Test
	void slotIsPossibleWithReservedForOthersSlot() {
		final User userToSlot = User.builder().build();
		final Slot sut = Slot.builder()
				.reservedFor(Guild.builder().build())
				.build();

		assertThat(sut.getSlottable(userToSlot))
				.extracting(Slottable::state)
				.isEqualTo(NO_RESERVED)
				.extracting(SlottableState::isSlottingAllowed)
				.isEqualTo(false);
	}

	@Test
	void getSlottableWithMissingOptionalRequirement() {
		final User userToSlot = User.builder().build();
		final Requirement requirement = Requirement.builder()
				.requirementList(RequirementList.builder().enforced(false).build())
				.build();
		final Slot sut = buildSlotWithRequirements(Set.of(requirement));

		assertThat(sut.getSlottable(userToSlot))
				.returns(YES_REQUIREMENTS_NOT_MET, Slottable::state)
				.returns(Set.of(requirement), Slottable::requirementsNotMet)
				.extracting(slottable -> slottable.state().isSlottingAllowed())
				.isEqualTo(true);
	}

	@Test
	void getSlottableWithMissingEnforcedRequirement() {
		final User userToSlot = User.builder().build();
		final Requirement requirement = Requirement.builder()
				.requirementList(RequirementList.builder().enforced(true).build())
				.build();
		final Slot sut = buildSlotWithRequirements(Set.of(requirement));

		assertThat(sut.getSlottable(userToSlot))
				.returns(NO_REQUIREMENTS_NOT_MET, Slottable::state)
				.returns(Set.of(requirement), Slottable::requirementsNotMet)
				.extracting(slottable -> slottable.state().isSlottingAllowed())
				.isEqualTo(false);
	}

	@Test
	void slotIsPossibleWithEmptySlot() {
		final User userToSlot = User.builder().build();
		final Slot sut = buildEmptySlot();

		assertThat(sut.getSlottable(userToSlot))
				.extracting(Slottable::state)
				.isEqualTo(YES)
				.extracting(SlottableState::isSlottingAllowed)
				.isEqualTo(true);
	}

	//slotWithoutUpdate
	@Test
	void slotSlots() {
		final User userToSlot = User.builder().build();
		final Slot sut = buildEmptySlot();

		assertThat(sut.getUser()).isNull();
		sut.slotWithoutUpdate(userToSlot);
		assertThat(userToSlot).isEqualTo(sut.getUser());
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

	/**
	 * Builds an empty but initialized slot to prevent NPEs
	 */
	private static Slot buildEmptySlot() {
		final Slot slot = Slot.builder()
				.requirements(Collections.emptySet())
				.build();
		final Squad squad = Squad.builder()
				.slotList(List.of(slot))
				.requirements(Collections.emptySet())
				.build();
		final Event event = Event.builder()
				.details(Collections.emptyList())
				.squadList(List.of(squad))
				.requirements(Collections.emptySet())
				.build();
		event.setBackReferences();
		return slot;
	}

	/**
	 * Adds the given user to a new guild and returns the guild
	 */
	private static Guild userInGuild(User user) {
		final Guild guild = Guild.builder().build();
		user.setGuilds(Set.of(GuildUser.builder().guild(guild).user(user).build()));
		return guild;
	}
}
