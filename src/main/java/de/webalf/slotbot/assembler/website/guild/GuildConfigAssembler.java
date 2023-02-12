package de.webalf.slotbot.assembler.website.guild;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.dtos.website.guild.GuildConfigDto;
import lombok.experimental.UtilityClass;

import java.util.Collections;

/**
 * @author Alf
 * @since 12.02.2023
 */
@UtilityClass
public final class GuildConfigAssembler {
	public GuildConfigDto toDto(Guild guild) {
		return GuildConfigDto.builder()
				.language(guild.getLanguage())
				.eventTypes(Collections.emptyList()) //TODO add guild specific event types
				.build();
	}
}
