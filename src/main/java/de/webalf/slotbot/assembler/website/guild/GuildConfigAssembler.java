package de.webalf.slotbot.assembler.website.guild;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.dtos.website.guild.GuildConfigDto;
import de.webalf.slotbot.util.LongUtils;
import lombok.experimental.UtilityClass;

/**
 * @author Alf
 * @since 12.02.2023
 */
@UtilityClass
public final class GuildConfigAssembler {
	public GuildConfigDto toDto(Guild guild) {
		return GuildConfigDto.builder()
				.language(guild.getLanguage())
				.archiveChannel(LongUtils.toString(guild.getArchiveChannel()))
				.memberRole(LongUtils.toString(guild.getMemberRole()))
				.eventManageRole(LongUtils.toString(guild.getEventManageRole()))
				.adminRole(LongUtils.toString(guild.getAdminRole()))
				.build();
	}
}
