package de.webalf.slotbot.model.bot;

import lombok.Value;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * Copy of {@link OptionData} to support translation keys in name and description
 *
 * @author Alf
 * @since 07.01.2023
 */
@Value
public class TranslatableOptionData {
	OptionType type;
	String name;
	String description;
	boolean isRequired;

	public OptionData toOptionData(String name, String description) {
		return new OptionData(type, name, description, isRequired);
	}
}
