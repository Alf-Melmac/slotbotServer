package de.webalf.slotbot.model.dtos.referenceless;

import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import de.webalf.slotbot.model.dtos.UserDto;
import de.webalf.slotbot.util.LongUtils;
import de.webalf.slotbot.util.bot.MentionUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;

import static de.webalf.slotbot.util.MaxLength.TEXT;

/**
 * @author Alf
 * @since 13.04.2021
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class SlotReferencelessDto extends AbstractIdEntityDto {
	@Size(max = TEXT)
	private final String name;

	private final int number;

	private final UserDto user;

	@Size(max = TEXT)
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

		slotText.append(":");

		final boolean isBlocked = !isEmpty && LongUtils.parseLong(getUser().getId()) == User.DEFAULT_USER_ID;
		if (!isEmpty && !isBlocked) {
			slotText.append(" **").append(MentionUtils.getUserAsMention(getUser().getId())).append("**");
		} else if (isBlocked) {
			slotText.append(" *").append(getReplacementText()).append("*");
		}
		return slotText;
	}
}
