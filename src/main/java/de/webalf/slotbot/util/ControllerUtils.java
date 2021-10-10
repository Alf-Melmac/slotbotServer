package de.webalf.slotbot.util;

import de.webalf.slotbot.controller.website.StartWebController;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 23.09.2021
 */
@UtilityClass
public final class ControllerUtils {
	public static void addLayoutSettings(@NonNull ModelAndView mav) {
		mav.addObject("startUrl", linkTo(methodOn(StartWebController.class).getStart()).toUri().toString());
		mav.addObject("group", GuildUtils.getCurrentGroupIdentifier());
	}
}
