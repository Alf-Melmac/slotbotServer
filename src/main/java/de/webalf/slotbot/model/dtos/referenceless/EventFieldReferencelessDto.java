package de.webalf.slotbot.model.dtos.referenceless;

import de.webalf.slotbot.model.dtos.AbstractIdEntityDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author Alf
 * @since 13.04.2021
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@Data
@SuperBuilder
public class EventFieldReferencelessDto extends AbstractIdEntityDto {
	@NotBlank
	@Size(max = MessageEmbed.TITLE_MAX_LENGTH)
	private String title;

	@NotBlank
	@Size(max = MessageEmbed.VALUE_MAX_LENGTH)
	private String text;

	private String link;
}
