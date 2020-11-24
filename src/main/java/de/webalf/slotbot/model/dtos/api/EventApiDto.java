package de.webalf.slotbot.model.dtos.api;

import de.webalf.slotbot.model.dtos.EventDto;
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
public class EventApiDto extends EventDto {
	@NotBlank
	private final String url;

	//Replacement for missionType and respawn
	@Size(max = 94)
	private final String missionTypeAndRespawn;

	private String modPackUrl;
}