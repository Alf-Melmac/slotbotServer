package de.webalf.slotbot.service;

import de.webalf.slotbot.configuration.properties.StorageProperties;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Alf
 * @since 24.11.2020
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FileService {
	private final StorageProperties storageProperties;

	/**
	 * Returns the given file by name as a {@link Resource}
	 *
	 * @param filename file to load
	 * @return file as {@link Resource}
	 * @throws ResourceNotFoundException if file doesn't exists or the path is a malformedURL
	 */
	public Resource loadAsResource(String filename) {
		final Path file = Paths.get(storageProperties.getLocation()).resolve(filename);

		try {
			final Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new ResourceNotFoundException(filename);
			}
		} catch (MalformedURLException e) {
			throw new ResourceNotFoundException(filename);
		}
	}
}
