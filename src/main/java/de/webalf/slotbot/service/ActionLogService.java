package de.webalf.slotbot.service;

import de.webalf.slotbot.model.ActionLog;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.enums.LogAction;
import de.webalf.slotbot.repository.ActionLogRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotEmpty;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author Alf
 * @since 06.09.2020
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ActionLogService {
	private final ActionLogRepository logRepository;

	void logEventAction(@NonNull LogAction action, @NonNull Event event, @NonNull User user) {
		logAction(action, Duration.between(LocalDateTime.now(), event.getDateTime()), event.getId(), user);
	}

	void logEventAction(@NonNull LogAction action, @NonNull Event event, @NonNull User user1, @NonNull User user2) {
		Duration timeGap = Duration.between(LocalDateTime.now(), event.getDateTime());
		logAction(action, timeGap, event.getId(), user1);
		logAction(action, timeGap, event.getId(), user2);
	}

	private void logAction(@NonNull LogAction action, @NonNull Duration timeGap, @NotEmpty long actionObjectId, @NonNull User user) {
		if (user.isDefaultUser()) {
			return;
		}

		ActionLog actionLog = ActionLog.builder()
				.action(action)
				.timeGap(timeGap)
				.actionObjectId(actionObjectId)
				.user(user)
				.build();
		logRepository.save(actionLog);
	}
}
