package de.webalf.slotbot.assembler.website;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.dtos.website.GuildDetailsDto;
import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 17.01.2023
 */
@UtilityClass
public final class GuildDetailsAssembler {
	public GuildDetailsDto toDto(Guild guild) {
		return GuildDetailsDto.builder()
				.id(guild.getIdString())
				.advanced(guild.isAdvanced())
				.baseUrl(guild.getBaseUrl())
				.groupIdentifier(guild.getGroupIdentifier())
				.emojiUrl(guild.buildEmojiUrl())
				.build();
	}
}
