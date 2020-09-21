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

/**
 * @author Alf
 * @since 06.09.2020
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ActionLogService {
	private final ActionLogRepository logRepository;

	void logEventAction(@NonNull LogAction action, @NonNull Event event, User user) {
		logAction(action, event.getId(), user);
	}

	private void logAction(@NonNull LogAction action, @NotEmpty long actionObjectId, User user) {
		ActionLog actionLog = ActionLog.builder()
				.action(action)
				.actionObjectId(actionObjectId)
				.user(user)
				.build();
		logRepository.save(actionLog);
	}
}
