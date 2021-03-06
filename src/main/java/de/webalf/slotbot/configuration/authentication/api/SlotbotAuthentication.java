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
@Component
@NoArgsConstructor
@AllArgsConstructor
public class SlotbotAuthentication implements Authentication {
	private static final long serialVersionUID = 7753355760596674581L;
	@Getter
	private String credentials;
	@Getter
	private Collection<? extends GrantedAuthority> authorities;

	public SlotbotAuthentication(String token) {
		credentials = token;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Object getDetails() {
		return null;
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

	}
}