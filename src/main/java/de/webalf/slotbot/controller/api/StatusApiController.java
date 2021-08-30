package de.webalf.slotbot.controller.api;

import de.webalf.slotbot.model.JobInfo;
import de.webalf.slotbot.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.webalf.slotbot.constant.Urls.API;
import static de.webalf.slotbot.util.permissions.ApiPermissionHelper.HAS_ADMIN_PERMISSION;

/**
 * @author Alf
 * @since 14.08.2020
 */
@RequestMapping(API + "/status")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatusApiController {
	private final SchedulerService schedulerService;

	@GetMapping
	public ResponseEntity<Void> ping() {
		log.trace("ping");
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/jobs")
	@PreAuthorize(HAS_ADMIN_PERMISSION)
	public List<JobInfo> getAllRunningJobs() {
		return schedulerService.getAllRunningJobs();
	}

	@GetMapping("/triggers")
	@PreAuthorize(HAS_ADMIN_PERMISSION)
	public List<JobInfo> getAllRunningTriggers() {
		return schedulerService.getAllTriggers();
	}
}
