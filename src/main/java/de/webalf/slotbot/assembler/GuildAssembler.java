package de.webalf.slotbot.assembler;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.dtos.GuildDto;
import de.webalf.slotbot.util.LongUtils;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.StreamSupport;

import static de.webalf.slotbot.model.Guild.GUILD_PLACEHOLDER;

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
				.id(LongUtils.parseLong(guildDto.getId(), GUILD_PLACEHOLDER))
				.build();
	}

	public static GuildDto toDto(Guild guild) {
		if (guild == null) {
			return null;
		}

		return GuildDto.builder()
				.id(guild.getIdString())
				.groupIdentifier(guild.getGroupIdentifier())
				.emojiUrl(guild.buildEmojiUrl())
				.build();
	}

	public static List<GuildDto> toDtoList(Iterable<? extends Guild> guilds) {
		return StreamSupport.stream(guilds.spliterator(), false)
				.map(GuildAssembler::toDto)
				.toList();
	}
}
