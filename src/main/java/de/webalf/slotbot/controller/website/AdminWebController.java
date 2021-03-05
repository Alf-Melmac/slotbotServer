package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.service.external.BattlemetricsApiService;
import de.webalf.slotbot.service.external.ExternalServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

import static de.webalf.slotbot.controller.Urls.ADMIN;
import static de.webalf.slotbot.util.PermissionHelper.HAS_ROLE_ADMIN;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 30.12.2020
 */
@Controller
@RequestMapping(ADMIN)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AdminWebController {
	private final BattlemetricsApiService battlemetricsApiService;
	private final ExternalServerService externalServerService;

	@GetMapping
	@PreAuthorize(HAS_ROLE_ADMIN)
	public ModelAndView getAdminHtml() {
		ModelAndView mav = new ModelAndView("admin");

		mav.addObject("startUrl", linkTo(methodOn(StartWebController.class).getStart()).toUri().toString());
		mav.addObject("logsUrl", linkTo(methodOn(LogWebController.class).getLogsHtml()).toUri().toString());
		mav.addObject("serverToggleUrl", linkTo(methodOn(AdminWebController.class).postServerToggle(true, null)).toUri().toString().replace(Boolean.TRUE.toString(), "{online}"));

		mav.addObject("servers", battlemetricsApiService.getServers());

		final HttpStatus ping = externalServerService.ping();
		mav.addObject("serverManagerStatus", ping != null ? ping.value() + " " + ping.getReasonPhrase() : "No status received. Rejected?");
		mav.addObject("serverManagerPing", ping != null && ping.is2xxSuccessful());
		mav.addObject("serverManagerPingTime", LocalDateTime.now());

		return mav;
	}

	@PostMapping("/server/{online}")
	@PreAuthorize(HAS_ROLE_ADMIN)
	public ResponseEntity<Void> postServerToggle(@PathVariable(value = "online") boolean online,
												 @RequestBody String serverIp) {
		externalServerService.toggleServer(battlemetricsApiService.findIdentifierByFullIp(serverIp), !online);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
