package de.webalf.slotbot.service;

import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.dtos.website.guild.GuildConfigPutDto;
import de.webalf.slotbot.repository.GuildRepository;
import de.webalf.slotbot.util.DtoUtils;
import de.webalf.slotbot.util.LongUtils;
import de.webalf.slotbot.util.StringUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.webalf.slotbot.model.Guild.GUILD_PLACEHOLDER;
import static de.webalf.slotbot.util.ConstraintConstants.TEXT;

/**
 * @author Alf
 * @since 04.01.2022
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class GuildService {
	private final GuildRepository guildRepository;

	/**
	 * Creates a new guild
	 *
	 * @param groupIdentifier group identifier of the new guild
	 * @return newly created guild
	 */
	public Guild create(@NonNull String groupIdentifier) {
		final Guild guild = Guild.builder()
				.groupIdentifier(groupIdentifier)
				.build();
		return guildRepository.saveAndFlush(guild);
	}

	/**
	 * Finds the guild linked to the given Discord guild, or creates a new one if none is linked yet.
	 *
	 * @param discordId Discord snowflake of the guild
	 * @param name      Discord guild name, used to set/update {@link Guild#getGroupIdentifier()}
	 * @return found or newly created guild
	 */
	public Guild findOrCreateByDiscordId(long discordId, String name) {
		final Guild guild = guildRepository.findByDiscordId(discordId).orElseGet(() -> Guild.builder().id(discordId).discordId(discordId).build());
		guild.setGroupIdentifier(name.substring(0, Math.min(name.length(), TEXT)));
		return guildRepository.saveAndFlush(guild);
	}

	/**
	 * Returns the guild linked to the given Discord guild.
	 *
	 * @param discordId Discord snowflake of the guild
	 * @return guild linked to the given Discord guild
	 * @throws ResourceNotFoundException if no guild is linked to this Discord guild
	 */
	public Guild findExistingByDiscordId(long discordId) {
		return guildRepository.findByDiscordId(discordId).orElseThrow(ResourceNotFoundException::new);
	}

	public List<Guild> findAll() {
		return guildRepository.findAll();
	}

	public List<Guild> findAllExceptDefault() {
		return guildRepository.findAllByOrderByGroupIdentifier().stream().filter(guild -> guild.getId() != GUILD_PLACEHOLDER).toList();
	}

	/**
	 * Maps the ids of guilds to the date time of their {@link Event#getOwnerGuild() last owned event}. Doesn't include guilds without events.
	 */
	public Map<Long, LocalDateTime> findLastEventOfGuilds() {
		return guildRepository.findDistinctByOwnerGuildOrderByDateTimeDesc().stream() //[guildId, eventDateTime]
				.collect(Collectors.toMap(
						obj -> (long) obj[0],
						obj -> LocalDateTime.ofEpochSecond((long) obj[1], 0, ZoneOffset.UTC) //see LocalDateTimePersistenceConverter
				));
	}

	public Guild findByIdentifier(@NonNull String identifier) {
		return findByName(identifier).orElseThrow(ResourceNotFoundException::new);
	}

	public Guild findByIdentifier(Optional<String> identifier) {
		return identifier
				.map(this::findByIdentifier)
				.orElseGet(this::getDefault);
	}

	public long getIdByIdentifier(@NonNull String identifier) {
		return findByName(identifier).orElseThrow(ResourceNotFoundException::new).getId();
	}

	public long getIdByIdentifier(Optional<String> identifier) {
		return identifier
				.map(this::getIdByIdentifier)
				.orElse(GUILD_PLACEHOLDER);
	}

	/**
	 * Returns the guild associated with the given id
	 *
	 * @param id to find guild for
	 * @return Guild found by id
	 * @throws ResourceNotFoundException if no guild with this id could be found
	 */
	public Guild findExisting(long id) {
		return guildRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
	}

	public Optional<Guild> findByName(String name) {
		return guildRepository.findByGroupIdentifier(name);
	}

	/**
	 * Checks if there is any guild with the given discord id and any of the given role ids ({@link Guild#getMemberRole()},
	 * {@link Guild#getEventManageRole()}, {@link Guild#getAdminRole()}) is configured for this guild.
	 */
	public boolean existsByDiscordIdAndAnyRoleIn(long discordGuildId, Set<Long> roles) {
		return guildRepository.existsByDiscordIdAndAnyRoleIn(discordGuildId, roles);
	}

	public boolean isAdvanced(@NonNull String identifier) {
		return Boolean.TRUE.equals(guildRepository.isAdvancedByIdentifier(identifier));
	}

	public Guild evaluateReservedFor(String reservedFor) {
		return StringUtils.isNotEmpty(reservedFor) ? findExisting(Long.parseLong(reservedFor)) : null;
	}

	/**
	 * Updates the guild found by id with values from the {@link GuildConfigPutDto}
	 *
	 * @param guildId guild id
	 * @param dto     with values to update
	 * @return updated guild
	 */
	public Guild updateGuild(long guildId, @NonNull GuildConfigPutDto dto) {
		final Guild guild = findExisting(guildId);

		DtoUtils.ifPresent(dto.getLanguage(), guild::setLanguage);
		DtoUtils.ifPresent(dto.getArchiveChannel(), archiveChannelId -> guild.setArchiveChannel(LongUtils.parseLongWrapper(archiveChannelId)));
		DtoUtils.ifPresent(dto.getMemberRole(), memberRoleId -> guild.setMemberRole(LongUtils.parseLongWrapper(memberRoleId)));
		DtoUtils.ifPresent(dto.getEventManageRole(), eventManageRoleId -> guild.setEventManageRole(LongUtils.parseLongWrapper(eventManageRoleId)));
		DtoUtils.ifPresent(dto.getAdminRole(), adminRoleId -> guild.setAdminRole(LongUtils.parseLongWrapper(adminRoleId)));

		return guild;
	}

	/**
	 * Removes the archive channel configuration of a removed channel
	 *
	 * @param discordGuildId   guild the channel was removed from
	 * @param removedChannelId the removed channel
	 */
	@Async
	public void removeArchiveChannelByChannel(long discordGuildId, long removedChannelId) {
		final Guild guild = findExistingByDiscordId(discordGuildId);

		final Long archiveChannel = guild.getArchiveChannel();
		if (archiveChannel != null && (archiveChannel == removedChannelId)) {
			guild.setArchiveChannel(null);
		}
	}

	private static final String SLOTBOT_LOGO = "https://slotbot.de/backend/userContent/1/AM-name-slotbot-small.png";
	private static final String AMB_LOGO = "https://events.armamachtbock.de/backend/userContent/1/AM-Blau-small.jpg";
	private static final String DAA_LOGO = "https://events.deutsche-arma-allianz.de/backend/userContent/1/DAA_transparent.gif";
	private static final String TTT_LOGO = "https://events.tacticalteam.de/backend/userContent/1/slotbot_ttt-eventplatzhalter.png";

	public static String getLogo(@NonNull Guild guild) {
		return getLogo(guild.getId());
	}

	public static String getLogo(long guildId) {
		if (GuildService.isAMB(guildId)) {
			return AMB_LOGO;
		} else if (GuildService.isDAA(guildId)) {
			return DAA_LOGO;
		} else if (GuildService.isTTT(guildId)) {
			return TTT_LOGO;
		}
		return SLOTBOT_LOGO;
	}

	//Special snowflakes
	private Guild getDefault() {
		return guildRepository.findById(GUILD_PLACEHOLDER).orElseThrow(IllegalStateException::new);
	}

	private static boolean is(long guildId, long givenGuildId) {
		return guildId == givenGuildId;
	}

	private static final long AMB = 706254758721224707L;
	private static final long DAA = 874650742089203792L;
	private static final long TTT = 121399943393968128L;

	public static boolean isAMB(@NonNull Guild guild) {
		return isAMB(guild.getId());
	}

	public static boolean isAMB(long guildId) {
		return is(AMB, guildId);
	}

	private static boolean isDAA(long guildId) {
		return is(DAA, guildId);
	}

	private static boolean isTTT(long guildId) {
		return is(TTT, guildId);
	}
}
