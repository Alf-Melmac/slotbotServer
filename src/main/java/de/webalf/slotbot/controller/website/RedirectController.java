package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.model.Redirect;
import de.webalf.slotbot.repository.RedirectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Alf
 * @since 09.11.2020
 */
@Controller
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RedirectController {
	private final RedirectRepository redirectRepository;

	@GetMapping("/{link}")
	public RedirectView redirectToLink(@PathVariable(value = "link") String link) {
		RedirectView redirectView = new RedirectView();
		ServletUriComponentsBuilder contextPath = ServletUriComponentsBuilder.fromCurrentContextPath();
		contextPath.port(3000);
		redirectView.setUrl(redirectRepository.findByEndpoint(link)
				.orElse(Redirect.builder().link(contextPath.toUriString() + "/" + link).build())
				.getLink());
		return redirectView;
	}
}
