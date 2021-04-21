package de.webalf.slotbot.model.dtos.referenceless;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Alf
 * @since 13.04.2021
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class SquadReferencelessDto extends AbstractIdEntityDto {
	@Size(max = 80)
	private final String name;

	private final List<? extends SlotReferencelessDto> slotList;

	/**
	 * Prepares the squad to be used in the slotlist
	 *
	 * @return squad in discord message format
	 */
	public StringBuilder toSlotList() {
		StringBuilder squadText = new StringBuilder("**").append(getName()).append("**");
		for (SlotReferencelessDto slot : getSlotList()) {
			squadText.append("\n").append(slot.toSlotList());
		}
		return squadText;
	}
}
