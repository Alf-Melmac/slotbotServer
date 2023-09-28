package de.webalf.slotbot.frontend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.SessionInformationExpiredEvent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alf
 * @since 28.09.2023
 */
@ExtendWith(MockitoExtension.class)
class SessionExpiredTest {
	/**
	 * Frontend relies on the exact message of the expired session detection.
	 */
	@Test
	void validateExpiredSessionMessage() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, UnsupportedEncodingException {
		final Class<?> strategyClass = Class.forName(ConcurrentSessionFilter.class.getName() + "$ResponseBodySessionInformationExpiredStrategy");
		final Constructor<?> strategyConstructor = strategyClass.getDeclaredConstructor();
		strategyConstructor.setAccessible(true);

		final Method method = strategyClass.getDeclaredMethod("onExpiredSessionDetected", SessionInformationExpiredEvent.class);
		method.setAccessible(true);

		final MockHttpServletResponse response = Mockito.mock(MockHttpServletResponse.class);
		final StringWriter responseWriter = new StringWriter();
		Mockito.when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
		final SessionInformationExpiredEvent event = new SessionInformationExpiredEvent(new SessionInformation(new Object(), "sessionId", Date.from(Instant.EPOCH)), new MockHttpServletRequest(), response);

		method.invoke(strategyConstructor.newInstance(), event);

		assertEquals("This session has been expired (possibly due to multiple concurrent logins being attempted as the same user).", responseWriter.toString());
	}
}
