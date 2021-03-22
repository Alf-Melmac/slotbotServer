package de.webalf.slotbot.service;

import de.webalf.slotbot.configuration.properties.StorageProperties;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
	 * Searches in {@link StorageProperties#getDownload()} path
	 *
	 * @param filename file to load
	 * @return file as {@link Resource}
	 * @throws ResourceNotFoundException if file doesn't exists or the path is a malformedURL
	 */
	@Cacheable("fileLoader")
	public Resource loadAsResource(String filename) {
		final Path file = Paths.get(storageProperties.getDownload()).resolve(filename);

		return getResource(filename, file);
	}

	/**
	 * Returns the given file by name as a {@link Resource}
	 * Searches in {@link StorageProperties#getImages()} path
	 *
	 * @param path img path to load
	 * @return file as {@link Resource}
	 * @throws ResourceNotFoundException if file doesn't exists or the path is a malformedURL
	 */
	@Cacheable("imgLoader")
	public Resource loadImgAsResource(String path) {
		String imageStorage = storageProperties.getImages();

		final int lastSlash = path.lastIndexOf('/');
		if (lastSlash != -1) {
			imageStorage += path.substring(0, lastSlash);
		}
		final String imgName = path.substring(lastSlash + 1);

		return getResource(path, Paths.get(imageStorage).resolve(imgName));
	}

	/**
	 * @param filename only used for readable exception
	 * @param path to get resource from
	 * @return resource
	 * @throws ResourceNotFoundException if resource doesn't exists in this location or the path is a malformedURL
	 */
	private Resource getResource(String filename, Path path) {
		try {
			final Resource resource = new UrlResource(path.toUri());
			if (resource.exists() && resource.isReadable()) {
				return resource;
			} else {
				throw new ResourceNotFoundException(filename);
			}
		} catch (MalformedURLException e) {
			throw new ResourceNotFoundException(filename);
		}
	}
}
