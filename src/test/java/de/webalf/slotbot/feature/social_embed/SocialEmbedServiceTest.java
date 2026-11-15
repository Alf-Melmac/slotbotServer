package de.webalf.slotbot.feature.social_embed;

import de.webalf.slotbot.feature.social_embed.model.OpenGraph;
import de.webalf.slotbot.feature.social_embed.model.SocialEmbedType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alf
 * @since 02.11.2026
 */
class SocialEmbedServiceTest {
	private static final SocialEmbedType TYPE = SocialEmbedType.EVENTS;
	private static final long EXISTING_ID = 1L;
	private static final String URL = "https://slotbot.de/events/1";
	private static final String IMAGE_URL = "https://cdn.example/image.png";

	private OpenGraph meta;
	private SocialEmbedService sut;

	@BeforeEach
	void setUp() {
		meta = OpenGraph.builder().title("Title").url(URL).imageUrl(IMAGE_URL).build();
		final OpenGraphProvider provider = new OpenGraphProvider() {
			@Override
			public SocialEmbedType type() {
				return TYPE;
			}

			@Override
			public Optional<OpenGraph> resolve(long id) {
				return id == EXISTING_ID ? Optional.of(meta) : Optional.empty();
			}
		};
		sut = new SocialEmbedService(List.of(provider));
	}

	@Test
	void getMetaTagsOmitsMissingOptionalFields() {
		assertThat(sut.getMetaTags(TYPE, EXISTING_ID)).contains("""
				<meta property="og:site_name" content="Slotbot">
				<meta property="og:url" content="https://slotbot.de/events/1">
				<meta property="og:title" content="Title">
				<meta property="og:image" content="https://cdn.example/image.png">
				<meta name="theme-color" content="#00FFFF">
				<meta name="twitter:card" content="summary">""");
	}

	@Test
	void getMetaTagsUsesResolvedFields() {
		meta = OpenGraph.builder()
				.type("profile")
				.title("Title")
				.url(URL)
				.description("Description")
				.imageUrl(IMAGE_URL)
				.locale(Locale.GERMANY)
				.themeColor("#AAAAAA")
				.build();

		assertThat(sut.getMetaTags(TYPE, EXISTING_ID)).contains("""
				<meta property="og:site_name" content="Slotbot">
				<meta property="og:type" content="profile">
				<meta property="og:url" content="https://slotbot.de/events/1">
				<meta property="og:title" content="Title">
				<meta property="og:description" content="Description">
				<meta property="og:image" content="https://cdn.example/image.png">
				<meta property="og:locale" content="de_DE">
				<meta name="theme-color" content="#AAAAAA">
				<meta name="twitter:card" content="summary">""");
	}

	@Test
	void getMetaTagsEscapesValues() {
		meta = OpenGraph.builder()
				.title("\"><script>alert(1)</script>")
				.url(URL)
				.imageUrl(IMAGE_URL)
				.build();

		assertThat(sut.getMetaTags(TYPE, EXISTING_ID)).hasValueSatisfying(tags -> assertThat(tags)
				.doesNotContain("<script>")
				.contains("content=\"&quot;&gt;&lt;script&gt;alert(1)&lt;/script&gt;\""));
	}

	@Test
	void getMetaTagsEmptyForMissingEntity() {
		assertThat(sut.getMetaTags(TYPE, 2L)).isEmpty();
	}

	@Test
	void getMetaTagsEmptyForTypeWithoutProvider() {
		assertThat(sut.getMetaTags(SocialEmbedType.GUILDS, EXISTING_ID)).isEmpty();
	}
}
