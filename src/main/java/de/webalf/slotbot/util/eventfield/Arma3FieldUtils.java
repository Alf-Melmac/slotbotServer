package de.webalf.slotbot.util.eventfield;

import de.webalf.slotbot.controller.website.FileWebController;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 27.04.2021
 */
@UtilityClass
@Slf4j
public final class Arma3FieldUtils {

	public static final Pattern FILE_PATTERN = Pattern.compile("^(Arma_3_Preset_)?(.+)\\.html");
	private static final Map<String, String> DOWNLOADABLE_MOD_SETS = new HashMap<>();

	public static void fillDownloadableModSets(Set<String> fileNames) {
		DOWNLOADABLE_MOD_SETS.clear();
		fileNames.forEach(fileName -> {
			final Matcher matcher = FILE_PATTERN.matcher(fileName);
			matcher.find();
			DOWNLOADABLE_MOD_SETS.put(matcher.group(2), fileName);
		});
		log.info("Found {} downloadable mod packs", DOWNLOADABLE_MOD_SETS.size());
	}

	/**
	 * Matches the given string to a known modSet url
	 *
	 * @param modSet  to get url for
	 * @param baseUrl fallback url if the current request doesn't have a mapping
	 * @return download url if known or null
	 */
	public static String getModSetUrl(String modSet, @NonNull String baseUrl) {
		if (modSet == null) {
			return null;
		}
		final String fileName = DOWNLOADABLE_MOD_SETS.get(modSet);
		if (fileName != null) {
			final String url = linkTo(methodOn(FileWebController.class).getFile(fileName)).toUri().toString();
			if (!url.startsWith("http")) {
				return baseUrl + url;
			}
			return url;
		}
		return null;
	}
}
