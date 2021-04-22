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
	//wildcard to allow api dto. May not be final to allow copying (EventApiAssembler#toActionDto)
	private List<? extends SquadReferencelessDto> squadList;

	private List<? extends EventFieldReferencelessDto> details;

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
