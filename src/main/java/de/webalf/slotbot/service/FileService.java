package de.webalf.slotbot.service;

import de.webalf.slotbot.configuration.properties.StorageProperties;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

/**
 * @author Alf
 * @since 24.11.2020
 */
@Service
@RequiredArgsConstructor
public class FileService {
	private final StorageProperties storageProperties;

	/**
	 * Returns the given file by name as a {@link Resource}
	 * Searches in {@link StorageProperties#getDownload()} path
	 *
	 * @param filename file to load
	 * @return file as {@link Resource}
	 * @throws ResourceNotFoundException if file doesn't exist or is outside the download directory
	 */
	@Cacheable("fileLoader")
	public Resource loadAsResource(String filename) {
		return getResource(storageProperties.getDownload(), filename);
	}

	/**
	 * Returns the given ics file by name as a {@link Resource}
	 * Searches in {@link StorageProperties#getCalendarOutput()} path
	 *
	 * @param filename ics file to load
	 * @return file as {@link Resource}
	 * @throws ResourceNotFoundException if file doesn't exist or is outside the calendar directory
	 */
	public Resource loadIcsAsResource(String filename) {
		return getResource(storageProperties.getCalendarOutput(), filename);
	}

	@Cacheable("userContentLoader")
	public Resource loadUserContentAsResource(String userId, String filename) {
		return getResource(storageProperties.getUserContent(), userId, filename);
	}

	/**
	 * Resolves the given path segments against the base directory and ensures the result stays inside of it
	 *
	 * @param baseDir  directory the resource must be located in
	 * @param segments path segments to resolve against the base directory. The last one is the filename, used for readable exceptions
	 * @return resource
	 * @throws ResourceNotFoundException if resource doesn't exist, isn't readable or is located outside the base directory
	 */
	private Resource getResource(String baseDir, String... segments) {
		final String filename = segments[segments.length - 1];
		final Path root = Path.of(baseDir).toAbsolutePath().normalize();
		final Path path;
		try {
			path = root.resolve(Path.of("", segments)).normalize();
		} catch (InvalidPathException _) {
			throw new ResourceNotFoundException(filename);
		}
		if (!path.startsWith(root)) {
			throw new ResourceNotFoundException(filename);
		}

		final Resource resource = new FileSystemResource(path);
		if (resource.exists() && resource.isReadable()) {
			return resource;
		}
		throw new ResourceNotFoundException(filename);
	}
}
