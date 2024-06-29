package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.GuildAssembler;
import de.webalf.slotbot.assembler.website.GuildDetailsAssembler;
import de.webalf.slotbot.assembler.website.UserInGuildAssembler;
import de.webalf.slotbot.assembler.website.guild.GuildConfigAssembler;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.dtos.GuildDto;
import de.webalf.slotbot.model.dtos.website.GuildDetailsDto;
import de.webalf.slotbot.model.dtos.website.UserInGuildDto;
import de.webalf.slotbot.model.dtos.website.guild.GuildConfigDto;
import de.webalf.slotbot.model.dtos.website.guild.GuildConfigPutDto;
import de.webalf.slotbot.model.dtos.website.guild.GuildDiscordIntegrationDto;
import de.webalf.slotbot.model.dtos.website.pagination.FrontendPageable;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.service.GuildUsersService;
import de.webalf.slotbot.service.integration.GuildDiscordService;
import de.webalf.slotbot.util.StringUtils;
import de.webalf.slotbot.util.permissions.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * @author Alf
 * @since 23.07.2022
 */
@RestController
@RequestMapping("/guilds")
@RequiredArgsConstructor
@Slf4j
public class GuildController {
	private final GuildService guildService;
	private final GuildDiscordService guildDiscordService;
	private final GuildUsersService guildUsersService;
	private final UserInGuildAssembler userInGuildAssembler;

	@GetMapping
	public List<GuildDto> getGuilds() {
		return GuildAssembler.toDtoList(guildService.findAllExceptDefault());
	}

	@GetMapping("/{id}")
	public GuildDetailsDto getGuild(@PathVariable(value = "id") long guildId) {
		return GuildDetailsAssembler.toDto(guildService.findExisting(guildId));
	}

	@GetMapping("/{id}/config")
	@PreAuthorize("@permissionChecker.hasAdminPermission(#guildId)")
	public GuildConfigDto getGuildConfig(@PathVariable(value = "id") long guildId) {
		return GuildConfigAssembler.toDto(guildService.findExisting(guildId));
	}

	@PutMapping("/{id}/config")
	@PreAuthorize("@permissionChecker.hasAdminPermission(#guildId)")
	public GuildConfigDto putGuildConfig(@PathVariable(value = "id") long guildId, @RequestBody GuildConfigPutDto guildConfig) {
		return GuildConfigAssembler.toDto(guildService.updateGuild(guildId, guildConfig));
	}

	@GetMapping("/{id}/discord")
	@PreAuthorize("@permissionChecker.hasAdminPermission(#guildId)")
	public GuildDiscordIntegrationDto getDiscordIntegration(@PathVariable(value = "id") long guildId) {
		final boolean connected = guildDiscordService.isConnected(guildId);
		return GuildDiscordIntegrationDto.builder()
				.connected(connected)
				.categories(connected ? guildDiscordService.getGuildChannels(guildId) : Collections.emptyList())
				.allowedToManageRoles(connected && guildDiscordService.isAllowedToManageRoles(guildId))
				.roles(connected ? guildDiscordService.getGuildRoles(guildId) : Collections.emptyList())
				.build();
	}

	@GetMapping("/{id}/users")
	public FrontendPageable<UserInGuildDto> getGuildUsers(@PathVariable(value = "id") long guildId, Pageable pageRequest) {
		final Guild guild = guildService.findExisting(guildId);
		return FrontendPageable.of(guildUsersService.findGuildUsers(guild, pageRequest)
				.map(user -> userInGuildAssembler.toDto(user, guild)));
	}

	@PutMapping(value = "/{id}/users/{userId}", consumes = TEXT_PLAIN_VALUE)
	@PreAuthorize("@permissionChecker.hasAdminPermission(#guildId)")
	public void putGuildUserRole(@PathVariable(value = "id") long guildId, @PathVariable(value = "userId") long userId, @RequestBody(required = false) String role) {
		guildUsersService.setRole(guildId, userId, StringUtils.isEmpty(role) ? null : Role.valueOf(role));
	}

	@DeleteMapping("/{id}/users/{userId}")
	@PreAuthorize("@permissionChecker.hasAdminPermission(#guildId)")
	public void deleteGuildUser(@PathVariable(value = "id") long guildId, @PathVariable(value = "userId") long userId) {
		guildUsersService.remove(guildId, userId);
	}
}
