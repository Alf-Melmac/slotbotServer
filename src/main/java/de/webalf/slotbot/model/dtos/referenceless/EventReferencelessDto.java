package de.webalf.slotbot.model.dtos.referenceless;

import de.webalf.slotbot.model.dtos.AbstractEventDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
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
	private List<? extends EventFieldReferencelessDto> details;

	private List<? extends SquadReferencelessDto> squadList;

	/**
	 * Returns the slotlist as content of a Discord message
	 * The list includes messages that do not exceed the discord message size limit
	 *
	 * @return slotlist
	 */
	public List<String> getSlotList() {
		StringBuilder slotListText = new StringBuilder("__**Teilnahmeplatzaufzählung**__");
		List<String> messages = new ArrayList<>();
		for (SquadReferencelessDto squad : getSquadList()) {
			final StringBuilder squadText = squad.toSlotList();
			if (slotListText.length() + 2 + squadText.length() > Message.MAX_CONTENT_LENGTH) {
				messages.add(slotListText.toString());
				slotListText = new StringBuilder();
			}
			slotListText.append("\n\n").append(squadText);
		}
		messages.add(slotListText.toString());

		return messages;
	}
}
