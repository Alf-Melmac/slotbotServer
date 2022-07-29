package de.webalf.slotbot.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alf
 * @since 29.07.2022
 */
@RestController
@RequestMapping("/authentication")
public class AuthenticationController {
	@GetMapping
	public String user(@AuthenticationPrincipal OAuth2User oAuth2User) {
		if (oAuth2User == null) {
			return null;
		}
		return oAuth2User.getAttribute("id");
	}
}
