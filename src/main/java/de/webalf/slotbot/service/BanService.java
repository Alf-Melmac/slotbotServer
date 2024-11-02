package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Ban;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.enums.LogAction;
import de.webalf.slotbot.model.event.BanEvent;
import de.webalf.slotbot.repository.BanRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alf
 * @since 13.10.24
 */
@Service
@Transactional
@RequiredArgsConstructor
public class BanService {
	private final BanRepository banRepository;
	private final UserService userService;
	private final GuildService guildService;
	private final ApplicationEventPublisher eventPublisher;
	private final ActionLogService actionLogService;

	public List<Ban> findByGuild(@NonNull Guild guild) {
		return banRepository.findByGuild(guild);
	}

	/**
	 * Checks if the given user is banned globally
	 *
	 * @param user to check ban for
	 * @return true if a ban entry exists
	 */
	public boolean isBanned(@NonNull User user) {
		return banRepository.existsByUserAndGuildNull(user);
	}

	/**
	 * Checks if the given user is banned globally or in one of the given guilds
	 *
	 * @param user   to check ban for
	 * @param guilds in which the ban should be checked
	 * @return true if a ban entry exists
	 */
	public boolean isBanned(@NonNull User user, @NonNull Guild... guilds) {
		return banRepository.existsByUserAndGuildInOrUser(user, guilds);
	}

	/**
	 * Bans the given user
	 *
	 * @param userId  to ban
	 * @param guildId (optional) in which the user should be banned
	 * @param reason  for the ban
	 */
	public void ban(long userId, Long guildId, String reason) {
		final User user = userService.findExisting(userId);
		final Guild guild = guildId == null ? null : guildService.findExisting(guildId);
		banRepository.save(Ban.builder()
				.user(user)
				.guild(guild)
				.reason(reason)
				.build());
		actionLogService.logAction(LogAction.BAN, userId, userService.getLoggedIn());
		eventPublisher.publishEvent(new BanEvent(userId, guildId));
	}
}
