package de.webalf.slotbot.service;

import de.webalf.slotbot.configuration.properties.StorageProperties;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;

/**
 * @author Alf
 * @since 24.11.2020
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
	private final StorageProperties storageProperties;

	/**
	 * Returns the given file by name as a {@link Resource}
	 * Searches in {@link StorageProperties#getDownload()} path
	 *
	 * @param filename file to load
	 * @return file as {@link Resource}
	 * @throws ResourceNotFoundException if file doesn't exist or the path is a malformedURL
	 */
	@Cacheable("fileLoader")
	public Resource loadAsResource(String filename) {
		final Path file = Path.of(storageProperties.getDownload()).resolve(filename);

		return getResource(filename, file);
	}

	/**
	 * Returns the given ics file by name as a {@link Resource}
	 * Searches in {@link StorageProperties#getCalendarOutput()} path
	 *
	 * @param filename ics file to load
	 * @return file as {@link Resource}
	 * @throws ResourceNotFoundException if file doesn't exist or the path is a malformedURL
	 */
	public Resource loadIcsAsResource(String filename) {
		final Path file = Path.of(storageProperties.getCalendarOutput()).resolve(filename);

		return getResource(filename, file);
	}

	@Cacheable("userContentLoader")
	public Resource loadUserContentAsResource(String userId, String filename) {
		final Path path = Path.of(storageProperties.getUserContent(), userId, filename);

		return getResource(filename, path);
	}

	/**
	 * @param filename only used for readable exception
	 * @param path     to get resource from
	 * @return resource
	 * @throws ResourceNotFoundException if resource doesn't exist in this location or the path is a malformedURL
	 */
	private Resource getResource(String filename, Path path) {
		try {
			final Resource resource = new UrlResource(path.toUri());
			if (resource.exists() && resource.isReadable()) {
				return resource;
			} else {
				throw new ResourceNotFoundException(filename);
			}
		} catch (MalformedURLException _) {
			throw new ResourceNotFoundException(filename);
		}
	}
}
