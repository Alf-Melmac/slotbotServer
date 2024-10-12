package de.webalf.slotbot.service;

import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.BlogPost;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.repository.BlogPostRepository;
import de.webalf.slotbot.util.permissions.PermissionHelper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URLDecoder;
import java.util.regex.Pattern;

import static de.webalf.slotbot.service.ImageService.ALLOWED_FILE_NAME_CHARS;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Alf
 * @since 30.06.2024
 */
@Service
@Transactional
@RequiredArgsConstructor
public class BlogService {
	private final GuildService guildService;
	private final BlogPostRepository blogPostRepository;

	private static final Safelist SAFELIST = Safelist.none();

	static {
		SAFELIST.addTags("p", "br", "strong", "em", "u", "s", "mark", "h1", "h2", "h3", "ul", "li", "ol", "blockquote", "hr", "a", "img")
				.addAttributes("p", "style")
				.addAttributes("h1", "style")
				.addAttributes("h2", "style")
				.addAttributes("h3", "style")
				.addAttributes("a", "href", "target", "rel")
				.addAttributes("img", "src")

				.addProtocols("a", "href", "https")

				.addEnforcedAttribute("a", "rel", "noopener noreferrer nofollow")
				.addEnforcedAttribute("img", "loading", "lazy")
				.addEnforcedAttribute("img", "style", "max-width: 100%;");
	}

	private static String cleanContent(String content) {
		final String cleaned = Jsoup.clean(content, SAFELIST);

		if (Jsoup.parse(cleaned).select("img").stream().anyMatch(img -> {
			final String src = img.attr("src");

			final String fileName = src.substring(src.lastIndexOf("/") + 1);
			if (!URLDecoder.decode(fileName, UTF_8).matches("^[" + ALLOWED_FILE_NAME_CHARS + "]+$")) {
				return true;
			}

			final String uriString = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
			return !Pattern.compile("^" + uriString + "/userContent/" + PermissionHelper.getLoggedInUserId() + "/" + fileName + "$").matcher(src).matches();
		})) {
			throw new IllegalArgumentException();
		}
		return cleaned;
	}

	/**
	 * Returns the blog posts of the given guild in order
	 */
	public Page<BlogPost> findAll(String guild, Pageable pageable) {
		return blogPostRepository.findByGuildOrderByPinnedDescTimestampDesc(guildService.findByIdentifier(guild), pageable);
	}

	/**
	 * Returns the blog post with the given id if the user has admin permissions
	 *
	 * @throws ForbiddenException if the user does not have {@link PermissionHelper#hasAdministratorPermission(long) admin permissions}
	 */
	private BlogPost findForAdmin(long id) {
		final BlogPost post = blogPostRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
		if (!PermissionHelper.hasAdministratorPermission(post.getGuild().getId())) {
			throw new ForbiddenException("Not an admin");
		}
		return post;
	}

	public BlogPost post(String identifier, String content) {
		final Guild guild = guildService.findByIdentifier(identifier);
		final BlogPost post = BlogPost.builder()
				.content(cleanContent(content))
				.guild(guild)
				.build();
		guild.getBlogPosts().addFirst(post);
		return blogPostRepository.save(post);
	}

	public BlogPost update(long id, String content) {
		final BlogPost post = findForAdmin(id);
		post.setContent(cleanContent(content));
		return post;
	}

	public void pin(long id) {
		final BlogPost post = findForAdmin(id);
		blogPostRepository.updateAllPinnedToFalseByGuild(post.getGuild());
		post.setPinned(true);
	}

	public void unpin(long id) {
		final BlogPost post = findForAdmin(id);
		post.setPinned(false);
	}

	public void delete(long id) {
		final BlogPost post = findForAdmin(id);
		blogPostRepository.delete(post);
	}
}
