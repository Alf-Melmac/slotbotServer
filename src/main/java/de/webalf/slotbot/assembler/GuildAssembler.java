package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.dtos.GuildDto;
import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 16.01.2022
 */
@UtilityClass
public final class GuildAssembler {
	public static Guild fromDto(GuildDto guildDto) {
		if (guildDto == null) {
			return null;
		}

		return Guild.builder()
				.id(guildDto.getId())
				.build();
	}

	public static GuildDto toDto(Guild guild) {
		if (guild == null) {
			return null;
		}

		return GuildDto.builder()
				.id(guild.getId())
				.build();
	}
}
