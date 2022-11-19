package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.model.Redirect;
import de.webalf.slotbot.repository.RedirectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

/**
 * @author Alf
 * @since 09.11.2020
 */
@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class RedirectController {
	private final RedirectRepository redirectRepository;

	@GetMapping("/{link}")
	public RedirectView redirectToLink(@PathVariable(value = "link") String link) {
		RedirectView redirectView = new RedirectView();
		Optional<Redirect> redirect = redirectRepository.findByEndpoint(link);
		if (redirect.isEmpty() && !link.equals("events")) {
			log.warn("!!!!! Redirect to {}", link);
			redirectView.setStatusCode(HttpStatus.NOT_FOUND);
			return redirectView;
		}
		redirectView.setUrl(redirect
				.orElseGet(() -> Redirect.builder()
						.link(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString() + "/" + link)
						.build())
				.getLink());
		return redirectView;
	}
}
