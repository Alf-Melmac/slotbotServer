package de.webalf.slotbot.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

/**
 * @author Alf
 * @since 31.01.2021
 */
@ConfigurationProperties("servermanager")
@Getter
@Setter
public class ServerManagerProperties {
	@NotBlank
	private String url;

	@NotBlank
	private String tokenName = "server-manager-auth-token";

	@NotBlank
	private String token;
}
