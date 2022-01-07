package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.model.external.ServerStatus;
import de.webalf.slotbot.service.external.BattlemetricsApiService;
import de.webalf.slotbot.service.external.BattlemetricsApiService.Server;
import de.webalf.slotbot.service.external.ExternalServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Set;

import static de.webalf.slotbot.constant.Urls.ADMIN;
import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_ROLE_ADMIN;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 30.12.2020
 */
@Controller
@RequestMapping(ADMIN)
@PreAuthorize(HAS_ROLE_ADMIN)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AdminWebController {
	private final BattlemetricsApiService battlemetricsApiService;
	private final ExternalServerService externalServerService;

	@GetMapping
	public ModelAndView getAdminHtml() {
		ModelAndView mav = new ModelAndView("admin");

		mav.addObject("logsUrl", linkTo(methodOn(LogWebController.class).getLogsHtml()).toUri().toString());
		mav.addObject("utilsUrl", linkTo(methodOn(AdminUtilsWebController.class).getAdminUtilsHtml()).toUri().toString());
		mav.addObject("serverToggleUrl", linkTo(methodOn(AdminWebController.class).postServerRestart(null)).toUri().toString());

		Set<Server> servers = battlemetricsApiService.getServers();
		final HttpStatus ping = externalServerService.ping();
		servers.add(Server.builder()
				.name("Server Manager Ping")
				.ip(ping != null ? ping.value() + " " + ping.getReasonPhrase() : "No status received. Rejected?")
				.status(ping != null && ping.is2xxSuccessful() ? ServerStatus.ONLINE : ServerStatus.OFFLINE)
				.updatedAt(LocalDateTime.now())
				.build());
		mav.addObject("servers", servers);

		return mav;
	}

	@PostMapping("/server")
	public ResponseEntity<Void> postServerRestart(@RequestBody String serverIp) {
		externalServerService.restartServer(battlemetricsApiService.findIdentifierByFullIp(serverIp));
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
