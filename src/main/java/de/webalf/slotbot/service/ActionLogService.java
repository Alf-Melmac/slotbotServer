package de.webalf.slotbot.service;

import de.webalf.slotbot.model.ActionLog;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.User;
import de.webalf.slotbot.model.enums.LogAction;
import de.webalf.slotbot.repository.ActionLogRepository;
import de.webalf.slotbot.util.DateUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/**
 * @author Alf
 * @since 06.09.2020
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ActionLogService {
	private final ActionLogRepository logRepository;

	void logEventAction(@NonNull LogAction action, @NonNull Event event, @NonNull User... users) {
		Duration timeGap = Duration.between(DateUtils.now(), event.getDateTime());
		for (User user : users) {
			logAction(action, timeGap, event.getId(), user);
		}
	}

	void logAction(@NonNull LogAction action, long actionObjectId, @NonNull User user) {
		logAction(action, null, actionObjectId, user);
	}

	private void logAction(@NonNull LogAction action, Duration timeGap, long actionObjectId, @NonNull User user) {
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

	void removeActionLogsByObjectId(long objectId) {
		logRepository.deleteByActionObjectId(objectId);
	}
}
