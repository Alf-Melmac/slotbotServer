package de.webalf.slotbot.controller.api;

import de.webalf.slotbot.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.webalf.slotbot.constant.Urls.API;

/**
 * @author Alf
 * @since 11.03.2023
 */
@RequestMapping(API + "/events/migration")
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventMigrationApiController {
	private final EventRepository eventRepository;

	boolean migrated = false;

	@GetMapping
	public void migrate() {
		if (migrated) {
			log.warn("Migration already done");
			return;
		}
		eventRepository.findAll().forEach(event -> {
			log.info("Migrating event: {}", event.getId());
			event.setDateTime(event.getDateTime().minusHours(1));
		});
		eventRepository.flush();
		migrated = true;
	}
}
