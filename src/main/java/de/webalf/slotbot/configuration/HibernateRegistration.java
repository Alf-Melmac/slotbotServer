package de.webalf.slotbot.configuration;

import de.webalf.slotbot.processor.HibernateInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author Alf
 * @since 29.12.2020
 */
@Configuration
@AllArgsConstructor
public class HibernateRegistration implements HibernatePropertiesCustomizer {
	private final HibernateInterceptor hibernateInterceptor;

	@Override
	public void customize(Map<String, Object> hibernateProperties) {
		hibernateProperties.put("hibernate.session_factory.interceptor", hibernateInterceptor);
	}
}
