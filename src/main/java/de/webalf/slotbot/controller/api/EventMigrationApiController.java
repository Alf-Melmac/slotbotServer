package de.webalf.slotbot.controller.api;

import de.webalf.slotbot.service.EventMigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.webalf.slotbot.constant.Urls.UNSTABLE;

/**
 * @author Alf
 * @since 31.05.2024
 */
@RequestMapping(UNSTABLE + "/events/migration")
@RestController
@RequiredArgsConstructor
public class EventMigrationApiController {
	private final EventMigrationService migrationService;

	@GetMapping
	public void startMigration() {
		migrationService.migrate();
	}
}
