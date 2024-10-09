package de.webalf.slotbot.service.api;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.util.permissions.ApiPermissionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Wrapper for {@link GuildService} to be used by the api endpoints
 *
 * @author Alf
 * @since 01.10.2024
 */
@Service
@Transactional
@RequiredArgsConstructor
public class GuildApiService {
	private final GuildService guildService;

	public Guild getTokenGuild() {
		return guildService.findExisting(ApiPermissionHelper.getTokenGuild());
	}
}
