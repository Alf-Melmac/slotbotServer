package de.webalf.slotbot.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Alf
 * @since 04.03.2021
 */
@ConfigurationProperties("opt")
@Data
public class OptProperties {
	private String googleCredentialsLocation;
}
