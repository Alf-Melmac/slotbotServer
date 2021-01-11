package de.webalf.slotbot.model.dtos;

import de.webalf.slotbot.model.User;
import de.webalf.slotbot.util.LongUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;

/**
 * @author Alf
 * @since 23.06.2020
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@SuperBuilder
public class SlotDto extends AbstractIdEntityDto {
	@Size(max = 80)
	private final String name;

	private final int number;

	private final SquadDto squad;

	private UserDto user;

	@Size(max = 80)
	private final String replacementText;

	/**
	 * Prepares the slot to be used in the slotlist
	 *
	 * @return slot in discord message format
	 */
	StringBuilder toSlotList() {
		StringBuilder slotText = new StringBuilder();

		final boolean isEmpty = getUser() == null;
		if (isEmpty) {
			slotText.append("**");
		}
		slotText.append(getNumber()).append(" ").append(getName());
		if (isEmpty) {
			slotText.append("**");
		}

		slotText.append(": ");

		final boolean isBlocked = !isEmpty && LongUtils.parseLong(getUser().getId()) == User.DEFAULT_USER_ID;
		if (!isEmpty && !isBlocked) {
			slotText.append("**<@").append(getUser().getId()).append(">**");
		} else if (isBlocked) {
			slotText.append("*").append(getReplacementText()).append("*");
		}
		return slotText;
	}
}
