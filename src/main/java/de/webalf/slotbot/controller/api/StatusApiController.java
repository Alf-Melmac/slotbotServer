package de.webalf.slotbot.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.webalf.slotbot.constant.Urls.API;

/**
 * @author Alf
 * @since 14.08.2020
 */
@RequestMapping(API + "/status")
@RestController
@Slf4j
public class StatusApiController {
	@GetMapping("")
	public ResponseEntity<Void> ping() {
		log.trace("ping");
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
