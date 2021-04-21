package de.webalf.slotbot.model.dtos.api;

import de.webalf.slotbot.model.dtos.referenceless.EventReferencelessDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author Alf
 * @since 04.11.2020
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class EventApiDto extends EventReferencelessDto {
	@NotBlank
	private final String url;

	//Replacement for missionType and respawn
	@Size(max = 94)
	private final String missionTypeAndRespawn;

	private String modPackUrl;

	/**
	 * Returns the slotlist as content of a Discord message
	 *
	 * @return slotlist
	 */
	public String getSlotList() {
		StringBuilder slotListText = new StringBuilder("__**Teilnahmeplatzaufzählung**__");
		for (SquadDto squad : getSquadList()) {
			slotListText.append("\n\n").append(squad.toSlotList());
		}
		return slotListText.toString();
	}
}