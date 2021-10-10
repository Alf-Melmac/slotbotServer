package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.service.EventTypeService;
import de.webalf.slotbot.service.FileService;
import de.webalf.slotbot.service.bot.EventNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static de.webalf.slotbot.constant.Urls.ADMIN;
import static de.webalf.slotbot.util.ControllerUtils.addLayoutSettings;
import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_ROLE_SYS_ADMIN;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 11.06.2021
 */
@Controller
@RequestMapping(ADMIN + "/utils")
@PreAuthorize(HAS_ROLE_SYS_ADMIN)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AdminUtilsWebController {
	private final FileService fileService;
	private final EventTypeService eventTypeService;
	private final EventNotificationService eventNotificationService;

	@GetMapping
	public ModelAndView getAdminUtilsHtml() {
		ModelAndView mav = new ModelAndView("adminUtils");

		mav.addObject("adminUrl", linkTo(methodOn(AdminWebController.class).getAdminHtml()).toUri().toString());
		mav.addObject("postActionUrl", linkTo(methodOn(AdminUtilsWebController.class)
				.postAction("null"))
				.toUri().toString()
				.replace("null", "{action}"));

		addLayoutSettings(mav);
		return mav;
	}

	@PostMapping("/{action}")
	public ResponseEntity<Void> postAction(@PathVariable String action) {
		if ("listFiles".equals(action)) {
			fileService.listFiles();
		} else if ("deleteUnusedEventTypes".equals(action)) {
			eventTypeService.deleteUnused();
		} else if ("rebuildEventNotifications".equals(action)) {
			eventNotificationService.rebuildAllNotifications();
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
