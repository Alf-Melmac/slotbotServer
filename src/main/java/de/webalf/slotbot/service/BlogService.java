package de.webalf.slotbot.service;

import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.model.BlogPost;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		SAFELIST.addTags("p", "br", "strong", "em", "u", "s", "mark", "h1", "h2", "h3", "ul", "li", "ol", "blockquote", "hr", "a")
				.addAttributes("p", "style")
				.addAttributes("a", "href", "target", "rel")

				.addProtocols("a", "href", "https")

				.addEnforcedAttribute("a", "rel", "noopener noreferrer nofollow");
	}

	/**
	 * Returns the blog posts of the {@link GuildService#findCurrentNonNullGuild() current guild} in order
	 */
	public Page<BlogPost> findAll(Pageable pageable) {
		return blogPostRepository.findByGuildOrderByPinnedDescTimestampDesc(guildService.findCurrentNonNullGuild(), pageable);
	}

	public BlogPost post(String content) {
		final Guild guild = guildService.findCurrentNonNullGuild();
		final BlogPost post = BlogPost.builder()
				.content(Jsoup.clean(content, SAFELIST))
				.guild(guild)
				.build();
		guild.getBlogPosts().addFirst(post);
		return blogPostRepository.save(post);
	}

	public BlogPost update(long id, String content) {
		final BlogPost post = blogPostRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
		final Guild guild = guildService.findCurrentNonNullGuild();
		if (!post.getGuild().equals(guild)) {
			throw new IllegalArgumentException();
		}

		post.setContent(Jsoup.clean(content, SAFELIST));
		return post;
	}

	public void pin(long id) {
		final BlogPost post = blogPostRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
		final Guild guild = guildService.findCurrentNonNullGuild();
		blogPostRepository.updateAllPinnedToFalseByGuild(guild);
		post.setPinned(true);
	}

	public void unpin(long id) {
		final BlogPost post = blogPostRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
		post.setPinned(false);
	}

	public void delete(long id) {
		blogPostRepository.deleteById(id);
	}
}
