package de.webalf.slotbot.controller.website;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static de.webalf.slotbot.constant.Urls.ADMIN;
import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_ROLE_ADMIN;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 20.11.2020
 */
@Controller
@RequestMapping(ADMIN + "/logs")
@PreAuthorize(HAS_ROLE_ADMIN)
public class LogWebController {
	@GetMapping
	public ModelAndView getLogsHtml() {
		ModelAndView mav = new ModelAndView("logs");

		mav.addObject("startUrl", linkTo(methodOn(StartWebController.class).getStart()).toUri().toString());
		mav.addObject("adminUrl", linkTo(methodOn(AdminWebController.class).getAdminHtml()).toUri().toString());

		return mav;
	}
}
