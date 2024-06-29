package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.BlogPostAssembler;
import de.webalf.slotbot.model.dtos.BlogPostDto;
import de.webalf.slotbot.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * @author Alf
 * @since 30.06.2024
 */
@RestController
@RequestMapping("/blog")
@RequiredArgsConstructor
public class BlogController {
	private final BlogService blogService;

	@GetMapping
	public List<BlogPostDto> getBlogPosts() {
		return BlogPostAssembler.toDtoList(blogService.findAll());
	}

	@PostMapping(consumes = TEXT_PLAIN_VALUE)
	@PreAuthorize("@permissionChecker.hasAdminPermissionInCurrentGuild()")
	public void postBlogPost(@RequestBody String content) {
		blogService.post(content);
	}
}
