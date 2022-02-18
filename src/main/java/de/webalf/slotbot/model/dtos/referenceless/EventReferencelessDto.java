package de.webalf.slotbot.model.dtos.referenceless;

import de.webalf.slotbot.model.dtos.AbstractEventDto;
import de.webalf.slotbot.util.ListUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.Arrays;
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
	 * Returns the slotlist for the given guild as content of a Discord message
	 * The list includes messages that do not exceed the discord message size limit
	 *
	 * @return slotlist
	 */
	public List<String> getSlotList(long guildId) {
		StringBuilder slotListText = new StringBuilder("__**Teilnahmeplatzaufzählung**__");
		List<String> messages = new ArrayList<>();
		for (SquadReferencelessDto squad : getSquadList()) {
			final StringBuilder squadText = squad.toSlotList(guildId);

			//Message can't fit new lines for new squad must start new message
			if (slotListText.length() + 2 > Message.MAX_CONTENT_LENGTH) {
				messages.add(slotListText.toString());
				slotListText = new StringBuilder();
			}

			//Existing message size + 2 new line + new line may not exceed maximum discord message size
			if (slotListText.length() + 2 + squadText.length() > Message.MAX_CONTENT_LENGTH) {
				slotListText.append("\n"); //New squad

				final List<String> splitSquadText = new ArrayList<>(Arrays.asList(squadText.toString().split("\\n"))); //Split squad text on every slot
				String nextSlotText = ListUtils.shift(splitSquadText);
				//noinspection ConstantConditions We know the squadText is bigger than the content length, nextSlotText will exist before reaching null values
				while (slotListText.length() + 1 + nextSlotText.length() < Message.MAX_CONTENT_LENGTH) { //Add slots until there is no more space in the message
					slotListText.append("\n").append(nextSlotText);
					nextSlotText = ListUtils.shift(splitSquadText);
				}

				messages.add(slotListText.toString());
				slotListText = new StringBuilder(nextSlotText).append("\n").append(String.join("\n", splitSquadText)); //Next slot list message must contain the rest of the squad
			} else {
				//First squad in new message doesn't need a leading new line
				if (slotListText.length() != 0) {
					slotListText.append("\n\n");
				}
				slotListText.append(squadText);
			}
		}
		messages.add(slotListText.toString());

		return messages;
	}
}
