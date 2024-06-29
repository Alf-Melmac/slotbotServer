package de.webalf.slotbot.service;

import de.webalf.slotbot.model.BlogPost;
import de.webalf.slotbot.model.Guild;
import de.webalf.slotbot.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

	public List<BlogPost> findAll() {
		return guildService.findCurrentNonNullGuild().getBlogPosts();
	}

	public void post(String content) {
		final Guild guild = guildService.findCurrentNonNullGuild();
		final BlogPost post = BlogPost.builder()
				.content(Jsoup.clean(content, SAFELIST))
				.guild(guild)
				.build();
		guild.getBlogPosts().addFirst(post);
	}
}
