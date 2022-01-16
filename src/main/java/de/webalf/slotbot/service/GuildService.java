package de.webalf.slotbot.service;

import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.dtos.AbstractEventDto;
import de.webalf.slotbot.repository.GuildRepository;
import de.webalf.slotbot.util.LongUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

	private List<Guild> findAllWithUrlPattern() {
		return guildRepository.findByUrlPatternIsNotNull();
	}

	private static final Set<String> UNKNOWN_GROUPS = new HashSet<>();

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

		if (!UNKNOWN_GROUPS.contains(currentUri)) {
			log.warn("Searched for unknown group with uri '{}'", currentUri);
			UNKNOWN_GROUPS.add(currentUri);
		}
		return null;
	}

	/**
	 * Finds the {@link #findCurrentGuild() current guild} or {@link #getDefault() default guild with placeholder id}} as a fallback
	 *
	 * @return current guild
	 */
	public Guild findCurrentNonNullGuild() {
		final Guild currentGuild = findCurrentGuild();
		return currentGuild != null ? currentGuild : getDefault();
	}

	public String getCurrentGroupIdentifier() {
		final Guild currentGuild = findCurrentGuild();
		return currentGuild != null ? currentGuild.getGroupIdentifier() : "Slotbot";
	}

	public long getCurrentGuildId() {
		final Guild currentGuild = findCurrentGuild();
		return currentGuild != null ? currentGuild.getId() : GUILD_PLACEHOLDER;
	}

	public Guild find(long id) {
		return guildRepository.findById(id)
				.orElseGet(() -> guildRepository.save(Guild.builder().id(id).build()));
	}

	public Guild findByDiscordGuild(long discordGuild) {
		return guildRepository.findById(discordGuild).orElseThrow(ResourceNotFoundException::new);
	}

	public Optional<Guild> findByName(String name) {
		return guildRepository.findByGroupIdentifier(name);
	}

	/**
	 * If {@link #findCurrentGuild()} can determine a guild, this one is always taken. Otherwise, the given {@link AbstractEventDto#getOwnerGuild()} is used
	 *
	 * @param ownerGuild owner guild of the event
	 * @return evaluated owner guild
	 */
	public Guild getOwnerGuild(String ownerGuild) {
		final Guild currentGuild = findCurrentNonNullGuild();
		return currentGuild.getId() != GUILD_PLACEHOLDER
				? currentGuild
				: find(LongUtils.parseLong(ownerGuild, GUILD_PLACEHOLDER));
	}

	public Guild getOwnerGuild(@NonNull AbstractEventDto event) {
		return getOwnerGuild(event.getOwnerGuild());
	}

	public Guild getOwnerGuild(@NonNull Event event) {
		//None optional field thus can just be returned
		return event.getOwnerGuild();
	}

	private static final String SLOTBOT_LOGO = "https://cdn.discordapp.com/attachments/759147249325572097/899740543603589130/AM-name-slotbot-small.png";
	private static final String AMB_LOGO = "https://cdn.discordapp.com/attachments/759147249325572097/885282179796566046/AM-Blau-small.jpg";
	private static final String DAA_LOGO = "https://cdn.discordapp.com/attachments/759147249325572097/899747640634376272/DAA_transparent.gif";

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

	private boolean is(long guildId) {
		final Optional<Guild> guild = guildRepository.findById(guildId);
		return guild.isPresent() && guild.get().is();
	}

	private static boolean is(long guildId, long givenGuildId) {
		return guildId == givenGuildId;
	}

	public boolean isSlotbot() {
		return !isAMB() && !isDAA();
	}

	//TODO Replace with 706254758721224707L
	private static final long AMB = 701094826657054752L;
	//TODO Replace with 874650742089203792L
	private static final long DAA = 889250639551528980L;

	public static boolean isAMB(@NonNull Guild guild) {
		return isAMB(guild.getId());
	}

	public static boolean isAMB(long guildId) {
		return is(AMB, guildId);
	}

	public boolean isAMB() {
		return is(AMB);
	}

	public static boolean isDAA(long guildId) {
		return is(DAA, guildId);
	}

	public boolean isDAA() {
		return is(DAA);
	}
}
