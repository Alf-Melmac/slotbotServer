package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.GuildAssembler;
import de.webalf.slotbot.model.dtos.GuildDto;
import de.webalf.slotbot.service.GuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_POTENTIALLY_ROLE_EVENT_MANAGE;

/**
 * @author Alf
 * @since 23.07.2022
 */
@RestController
@RequestMapping("/guilds")
@PreAuthorize(HAS_POTENTIALLY_ROLE_EVENT_MANAGE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GuildController {
	private final GuildService guildService;

	@GetMapping
	public List<GuildDto> getGuilds() {
		return GuildAssembler.toDtoList(guildService.findAllExceptDefault());
	}
}
