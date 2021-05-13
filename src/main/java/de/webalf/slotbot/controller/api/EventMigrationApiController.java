package de.webalf.slotbot.controller.api;

import de.webalf.slotbot.service.EventMigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.webalf.slotbot.constant.Urls.API;

/**
 * @author Alf
 * @since 14.05.2021
 */
@RequestMapping(API + "/events/migration")
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventMigrationApiController {
	private final EventMigrationService migrationService;

	@GetMapping
	public void startMigration() {
		migrationService.migrate();
	}
}
