package de.webalf.slotbot.feature.action_log;

import de.webalf.slotbot.feature.action_log.model.ActionLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

import static de.webalf.slotbot.constant.Urls.UNSTABLE;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.HAS_ADMIN_PERMISSION;

/**
 * @author Alf
 * @since 02.06.2025
 */
@RequestMapping(UNSTABLE + "/action-logs/migration")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ActionLogMigrationApiController {
	private final ActionLogRepository actionLogRepository;

	@GetMapping
	@PreAuthorize(HAS_ADMIN_PERMISSION)
	public void migrate() {
		final List<ActionLog> all = actionLogRepository.findByTimeGapOldNotNull();
		log.info("Found {} action logs", all.size());
		all.forEach(actionLog -> {
			log.info("Migrating {}", actionLog.getId());
			actionLog.setTimeGap(Duration.parse(actionLog.getTimeGapOld()));
			actionLog.setTimeGapOld(null);
			actionLogRepository.save(actionLog);
		});
	}
}
