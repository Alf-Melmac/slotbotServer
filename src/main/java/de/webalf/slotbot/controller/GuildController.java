package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.GuildAssembler;
import de.webalf.slotbot.assembler.website.GuildDetailsAssembler;
import de.webalf.slotbot.assembler.website.UserInGuildAssembler;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.dtos.GuildDto;
import de.webalf.slotbot.model.dtos.website.GuildDetailsDto;
import de.webalf.slotbot.model.dtos.website.UserInGuildDto;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.service.GuildUsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Alf
 * @since 23.07.2022
 */
@RestController
@RequestMapping("/guilds")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class GuildController {
	private final GuildService guildService;
	private final GuildUsersService guildUsersService;
	private final UserInGuildAssembler userInGuildAssembler;

	@GetMapping
	public List<GuildDto> getGuilds() {
		return GuildAssembler.toDtoList(guildService.findAllExceptDefault());
	}

	@GetMapping("/{id}")
	public GuildDetailsDto getGuild(@PathVariable(value = "id") long guildId) {
		return GuildDetailsAssembler.toDto(guildService.findByDiscordGuild(guildId));
	}

	@GetMapping("/{id}/users")
	public List<UserInGuildDto> getGuildUser(@PathVariable(value = "id") long guildId) {
		final Guild guild = guildService.findByDiscordGuild(guildId);
		return userInGuildAssembler.toDtoList(guildUsersService.getUsers(guild), guild);
	}
}
