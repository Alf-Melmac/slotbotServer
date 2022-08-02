package de.webalf.slotbot.model.enums;

/**
 * @author Alf
 * @see <a href="https://discord.com/developers/docs/resources/user#user-object">Discord Developers Docs - User Object</a>
 * @since 01.08.2022
 */
public enum DiscordUserObjectFields {
	ID,
	USERNAME,
	DISCRIMINATOR,
	AVATAR;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
