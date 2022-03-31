package de.webalf.slotbot.controller.advice;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * @author Alf
 * @since 31.03.2022
 */
@ControllerAdvice
@Order(1000)
public class DataBinderControllerAdvice {
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		String[] denyList = new String[]{"class.*", "Class.*", "*.class.*", "*.Class.*"};
		dataBinder.setDisallowedFields(denyList);
	}
}
