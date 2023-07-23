package de.webalf.slotbot.model.dtos.website.guild;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.webalf.slotbot.model.enums.Language;
import lombok.Data;

import java.util.Optional;

/**
 * @author Alf
 * @since 23.07.2023
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class GuildConfigPutDto {
	private Optional<Language> language;
	private Optional<String> archiveChannel;
}
