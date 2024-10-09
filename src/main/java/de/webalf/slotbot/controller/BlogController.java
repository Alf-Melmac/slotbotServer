package de.webalf.slotbot.controller;

import de.webalf.slotbot.assembler.BlogPostAssembler;
import de.webalf.slotbot.model.dtos.BlogPostDto;
import de.webalf.slotbot.model.dtos.website.pagination.FrontendPageable;
import de.webalf.slotbot.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static de.webalf.slotbot.util.permissions.ApplicationRole.HAS_POTENTIALLY_ROLE_ADMIN;
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

	@GetMapping("/{guild}")
	@PreAuthorize("@permissionChecker.isAdvancedGuild(#guild)")
	public FrontendPageable<BlogPostDto> getBlogPosts(@PathVariable String guild, Pageable pageRequest) {
		return FrontendPageable.of(blogService.findAll(guild, pageRequest)
				.map(BlogPostAssembler::toDto));
	}

	@PostMapping(value = "/{guild}", consumes = TEXT_PLAIN_VALUE)
	@PreAuthorize("@permissionChecker.hasAdminPermissionIn(#guild) AND @permissionChecker.isAdvancedGuild(#guild)")
	public BlogPostDto postBlogPost(@PathVariable String guild,
	                                @RequestBody String content) {
		return BlogPostAssembler.toDto(blogService.post(guild, content));
	}

	@PutMapping(value = "/{id}", consumes = TEXT_PLAIN_VALUE)
	@PreAuthorize(HAS_POTENTIALLY_ROLE_ADMIN)
	public BlogPostDto updateBlogPost(@PathVariable long id,
	                                  @RequestBody String content) {
		return BlogPostAssembler.toDto(blogService.update(id, content));
	}

	@PutMapping("/{id}/pin")
	@PreAuthorize(HAS_POTENTIALLY_ROLE_ADMIN)
	public void putBlogPostPin(@PathVariable long id) {
		blogService.pin(id);
	}

	@PutMapping("/{id}/unpin")
	@PreAuthorize(HAS_POTENTIALLY_ROLE_ADMIN)
	public void putBlogPostUnpin(@PathVariable long id) {
		blogService.unpin(id);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize(HAS_POTENTIALLY_ROLE_ADMIN)
	public void deleteBlogPost(@PathVariable long id) {
		blogService.delete(id);
	}
}
