package de.webalf.slotbot.model.event;

import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Slot;
import de.webalf.slotbot.model.User;
import lombok.Builder;
import lombok.NonNull;

/**
 * Event for slot user change in an event.
 *
 * @param event        event containing the slot
 * @param slot         slot where the user changed
 * @param currentUser  new user of the slot
 * @param previousUser previous user of the slot
 * @author Alf
 * @since 05.08.2023
 */
@Builder
public record SlotUserChangedEvent(@NonNull Event event, @NonNull Slot slot, User currentUser, User previousUser) {
	/**
	 * Checks if <code>currentUser</code> is present and is not the {@link User#isDefaultUser() default user}
	 */
	public boolean currentUserIs() {
		return currentUser != null && !currentUser.isDefaultUser();
	}

	/**
	 * Checks if <code>previousUser</code> is present and is not the {@link User#isDefaultUser() default user}
	 */
	public boolean previousUserIs() {
		return previousUser != null && !previousUser.isDefaultUser();
	}
}
