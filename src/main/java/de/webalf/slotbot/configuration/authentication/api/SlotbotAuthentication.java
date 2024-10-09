package de.webalf.slotbot.configuration.authentication.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author Alf
 * @since 23.09.2020
 */
@Getter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class SlotbotAuthentication implements Authentication {
	private String credentials;
	private Collection<? extends GrantedAuthority> authorities;
	private long guildId;

	public SlotbotAuthentication(String token) {
		credentials = token;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Object getDetails() {
		return guildId;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}

	@Override
	public boolean isAuthenticated() {
		return false;
	}

	@Override
	public void setAuthenticated(boolean arg0) {
		/*no op*/
	}
}