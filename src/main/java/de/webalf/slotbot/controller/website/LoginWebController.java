package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.service.web.RedirectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_ROLE_EVERYONE;

/**
 * @author Alf
 * @since 02.11.2022
 */
@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/login")
public class LoginWebController {
	private final RedirectService redirectService;

	@GetMapping
	@PreAuthorize(HAS_ROLE_EVERYONE)
	public RedirectView login(@RequestParam String redirectUrl) {
		return new RedirectView(redirectService.redirectTo(redirectUrl));
	}
}
