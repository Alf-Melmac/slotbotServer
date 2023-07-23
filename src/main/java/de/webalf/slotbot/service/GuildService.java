package de.webalf.slotbot.service;

import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.dtos.website.guild.GuildConfigPutDto;
import de.webalf.slotbot.repository.GuildRepository;
import de.webalf.slotbot.util.DtoUtils;
import de.webalf.slotbot.util.LongUtils;
import de.webalf.slotbot.util.StringUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static de.webalf.slotbot.model.Guild.GUILD_PLACEHOLDER;

/**
 * @author Alf
 * @since 04.01.2022
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class GuildService {
	private final GuildRepository guildRepository;

	public List<Guild> findAll() {
		return guildRepository.findAll();
	}

	public List<Guild> findAllExceptDefault() {
		return guildRepository.findAllByOrderByGroupIdentifier().stream().filter(guild -> guild.getId() != GUILD_PLACEHOLDER).toList();
	}

	private List<Guild> findAllWithUrlPattern() {
		return guildRepository.findByUrlPatternIsNotNull();
	}

	/**
	 * Finds the current guild matching the current context path
	 *
	 * @return guild or null
	 */
	private Guild findCurrentGuild() {
		final String currentUri = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();

		for (Guild guild : findAllWithUrlPattern()) {
			if (guild.getUrlPattern().matcher(currentUri).matches()) {
				return guild;
			}
		}

		log.warn("Searched for unknown group with uri '{}'", currentUri);
		return null;
	}

	/**
	 * Finds the {@link #findCurrentGuild() current guild} or {@link #getDefault() default guild with placeholder id} as a fallback
	 *
	 * @return current guild
	 */
	public Guild findCurrentNonNullGuild() {
		final Guild currentGuild = findCurrentGuild();
		return currentGuild != null ? currentGuild : getDefault();
	}

	public long getCurrentGuildId() {
		final Guild currentGuild = findCurrentGuild();
		return currentGuild != null ? currentGuild.getId() : GUILD_PLACEHOLDER;
	}

	public Guild find(long id) {
		return guildRepository.findById(id)
				.orElseGet(() -> guildRepository.save(Guild.builder().id(id).build()));
	}

	/**
	 * Returns the guild associated with the given guildId
	 *
	 * @param guildId to find guild for
	 * @return Guild found by id
	 * @throws ResourceNotFoundException if no guild with this guildId could be found
	 */
	public Guild findExisting(long guildId) {
		return guildRepository.findById(guildId).orElseThrow(ResourceNotFoundException::new);
	}

	public Guild findByDiscordGuild(long discordGuild) {
		return guildRepository.findById(discordGuild).orElseThrow(ResourceNotFoundException::new);
	}

	public Optional<Guild> findByName(String name) {
		return guildRepository.findByGroupIdentifier(name);
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

		return guild;
	}

	public void removeArchiveChannelByChannel(long guildId, long removedChannelId) {
		final Guild guild = find(guildId);

		final Long archiveChannel = guild.getArchiveChannel();
		if (archiveChannel != null && (archiveChannel == removedChannelId)) {
			guild.setArchiveChannel(null);

		}
	}

	private static final String SLOTBOT_LOGO = "https://cdn.discordapp.com/attachments/759147249325572097/899740543603589130/AM-name-slotbot-small.png";
	private static final String AMB_LOGO = "https://cdn.discordapp.com/attachments/759147249325572097/885282179796566046/AM-Blau-small.jpg";
	private static final String DAA_LOGO = "https://cdn.discordapp.com/attachments/759147249325572097/899747640634376272/DAA_transparent.gif";

	public static String getLogo(@NonNull Guild guild) {
		return getLogo(guild.getId());
	}

	public static String getLogo(long guildId) {
		if (GuildService.isAMB(guildId)) {
			return AMB_LOGO;
		} else if (GuildService.isDAA(guildId)) {
			return DAA_LOGO;
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

	public static boolean isAMB(@NonNull Guild guild) {
		return isAMB(guild.getId());
	}

	public static boolean isAMB(long guildId) {
		return is(AMB, guildId);
	}

	public static boolean isDAA(long guildId) {
		return is(DAA, guildId);
	}
}
