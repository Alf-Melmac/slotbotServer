package de.webalf.slotbot.model.dtos.referenceless;

import de.webalf.slotbot.model.dtos.AbstractEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author Alf
 * @since 13.04.2021
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class EventReferencelessDto extends AbstractEventDto {
	private final List<? extends SquadReferencelessDto> squadList; //wildcard to allow api dto

	private final List<? extends EventFieldReferencelessDto> details; //wildcard to allow api dto

	/**
	 * Returns the slotlist as content of a Discord message
	 *
	 * @return slotlist
	 */
	public String getSlotList() {
		StringBuilder slotListText = new StringBuilder("__**Teilnahmeplatzaufzählung**__");
		for (SquadReferencelessDto squad : getSquadList()) {
			slotListText.append("\n\n").append(squad.toSlotList());
		}
		return slotListText.toString();
	}
}
