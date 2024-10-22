package de.webalf.slotbot.service;

import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.repository.BanRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Alf
 * @since 13.10.24
 */
@Service
@Transactional
@RequiredArgsConstructor
public class BanService {
	private final BanRepository banRepository;

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
	 * Checks if the given user is banned globally or one of the given guilds
	 *
	 * @param user to check ban for
	 * @param guilds in which the ban should be checked
	 * @return true if a ban entry exists
	 */
	public boolean isBanned(@NonNull User user, @NonNull Guild... guilds) {
		return banRepository.existsByUserAndGuildInOrUser(user, guilds);
	}
}
