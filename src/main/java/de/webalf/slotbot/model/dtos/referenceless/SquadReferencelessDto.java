package de.webalf.slotbot.model.dtos.referenceless;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import de.webalf.slotbot.model.dtos.GuildDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;
import java.util.List;

import static de.webalf.slotbot.util.MaxLength.TEXT;

/**
 * @author Alf
 * @since 13.04.2021
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class SquadReferencelessDto extends AbstractIdEntityDto {
	@Size(max = TEXT)
	private final String name;

	private final List<? extends SlotReferencelessDto> slotList;

	private final GuildDto reservedFor;

	/**
	 * Prepares the squad to be used in the slotlist
	 *
	 * @param guildId to prepare slotlist for
	 * @return squad in discord message format
	 */
	public StringBuilder toSlotList(long guildId) {
		StringBuilder squadText = new StringBuilder("**").append(getName()).append("**");
		for (SlotReferencelessDto slot : getSlotList()) {
			squadText.append("\n").append(slot.toSlotList(guildId, reservedFor));
		}
		return squadText;
	}
}
