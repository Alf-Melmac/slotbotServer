package de.webalf.slotbot.service;

import de.webalf.slotbot.configuration.properties.StorageProperties;
import de.webalf.slotbot.exception.ResourceNotFoundException;
import de.webalf.slotbot.util.eventfield.Arma3FieldUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Alf
 * @since 24.11.2020
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
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
		final Path file = Paths.get(storageProperties.getDownload()).resolve(filename);

		return getResource(filename, file);
	}

	/**
	 * Returns the given file by name as a {@link Resource}
	 * Searches in {@link StorageProperties#getImages()} path
	 *
	 * @param path img path to load
	 * @return file as {@link Resource}
	 * @throws ResourceNotFoundException if file doesn't exist or the path is a malformedURL
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
	 * Returns the given file by name as a {@link Resource}
	 * Searches in {@link StorageProperties#getCalendarOutput()} path
	 *
	 * @param filename ics file to load
	 * @return file as {@link Resource}
	 * @throws ResourceNotFoundException if file doesn't exist or the path is a malformedURL
	 */
	public Resource loadIcsAsResource(String filename) {
		final Path file = Paths.get(storageProperties.getCalendarOutput()).resolve(filename);

		return getResource(filename, file);
	}

	/**
	 * Populates caches that need files from the file directories
	 */
	public void listFiles() {
		Arma3FieldUtils.fillDownloadableModPacks(listDownloadFilesAndFilter(Arma3FieldUtils.FILE_PATTERN));
	}

	/**
	 * Return file names of every file in the download directory (non-recursive) matching the given pattern
	 *
	 * @param filterPattern that the file name must match
	 * @return set of file names
	 */
	public Set<String> listDownloadFilesAndFilter(@NonNull Pattern filterPattern) {
		try (Stream<Path> list = Files.list(Paths.get(storageProperties.getDownload()))) {
			return list
					.filter(file -> Files.isRegularFile(file) && filterPattern.matcher(file.getFileName().toString()).matches())
					.map(Path::getFileName)
					.map(Path::toString)
					.collect(Collectors.toUnmodifiableSet());
		} catch (IOException e) {
			log.error("Failed to list download files", e);
		}
		return Collections.emptySet();
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
		} catch (MalformedURLException e) {
			throw new ResourceNotFoundException(filename);
		}
	}
}
