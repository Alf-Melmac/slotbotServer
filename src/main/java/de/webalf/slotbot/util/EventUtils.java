package de.webalf.slotbot.util;

import de.webalf.slotbot.controller.website.DownloadController;
import de.webalf.slotbot.exception.ForbiddenException;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.model.dtos.EventDto;
import de.webalf.slotbot.model.dtos.api.EventApiDto;
import de.webalf.slotbot.util.permissions.ApiPermissionHelper;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import static de.webalf.slotbot.util.DateUtils.DATE_FORMATTER;
import static de.webalf.slotbot.util.DateUtils.TIME_FORMATTER;
import static de.webalf.slotbot.util.bot.EmbedUtils.addField;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 10.11.2020
 */
@UtilityClass
public final class EventUtils {
	/**
	 * Checks if the event, if hidden, is allowed to be read. Permission check is made by {@link ApiPermissionHelper#hasReadPermission()}.
	 *
	 * @param eventDto event to check
	 * @throws ForbiddenException if the event is hidden and read permission is not given
	 */
	public static void assertApiAccessAllowed(@NonNull EventDto eventDto) throws ForbiddenException {
		final Boolean hidden = eventDto.getHidden();
		if (hidden != null && hidden && !ApiPermissionHelper.hasReadPermission()) {
			throw new ForbiddenException("Access Denied");
		}
	}

	/**
	 * Works just like {@link #assertApiAccessAllowed(EventDto)} but expects a {@link Event} object
	 *
	 * @see #assertApiAccessAllowed(EventDto)
	 */
	public static void assertApiAccessAllowed(@NonNull Event event) throws ForbiddenException {
		if (event.isHidden() && !ApiPermissionHelper.hasReadPermission()) {
			throw new ForbiddenException("Access Denied");
		}
	}

	/**
	 * Combines the two given parameters to one string that can be shown
	 *
	 * @return compound of the two event params if present
	 */
	public static String getMissionTypeRespawnString(String missionType, Boolean respawn) {
		String compound = "";
		boolean respawnExists = respawn != null;
		if (StringUtils.isNotEmpty(missionType)) {
			compound += missionType;
			if (respawnExists) {
				compound += ", ";
			}
		}
		if (respawnExists) {
			compound += Boolean.TRUE.equals(respawn) ? "Respawn" : "Kein Respawn";
		}
		return compound;
	}

	public static String getModPackUrl(String modPack) {
		if (modPack == null) {
			return null;
		}
		switch (modPack) {
			case "2008_ArmaMachtBock":
				return linkTo(methodOn(DownloadController.class).getFile("Arma_3_Preset_2008_ArmaMachtBock.html")).toUri().toString();
			case "2012_ArmaMachtBock":
				return linkTo(methodOn(DownloadController.class).getFile("Arma_3_Preset_2012_ArmaMachtBock_Full.html")).toUri().toString();
			case "2101_ArmaMachtBock":
				return linkTo(methodOn(DownloadController.class).getFile("Arma_3_Preset_2101_ArmaMachtBock_Full_v2.html")).toUri().toString();
			case "2103_ArmaMachtBock":
				return linkTo(methodOn(DownloadController.class).getFile("Arma_3_Preset_2103_ArmaMachtBock_Full.html")).toUri().toString();
			case "2102_Event":
				return linkTo(methodOn(DownloadController.class).getFile("Arma_3_Preset_2102_Event.html")).toUri().toString();
			case "Joined_Operations_2020":
				return linkTo(methodOn(DownloadController.class).getFile("Joined_Operations_2020v2.html")).toUri().toString();
			case "Alliance_2021v1":
				return linkTo(methodOn(DownloadController.class).getFile("Alliance_2021v1.html")).toUri().toString();
			default:
				return null;
		}
	}

	public static MessageEmbed buildDetailsEmbed(@NonNull EventApiDto event) {
		String thumbnail = event.getPictureUrl();
		if (org.springframework.util.StringUtils.isEmpty(thumbnail)) {
			thumbnail = "https://cdn.discordapp.com/attachments/759147249325572097/759147455483740191/AM-Blau-big-bananemitschokokuchen.jpg";
		}

		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setColor(new Color((int) (Math.random() * 0x1000000))) //May be removed in future. But signals an update during development
				.setTitle(event.getName(), fixUrl(event.getUrl()))
				.setDescription(event.getDescription())
				.setThumbnail(thumbnail)
				.setFooter("Mission von " + event.getCreator())
				.setTimestamp(Instant.now());

		if (Boolean.TRUE.equals(event.getHidden())) {
			embedBuilder.setImage("https://cdn.discordapp.com/attachments/759147249325572097/789151354920632330/hidden_event.jpg");
		}

		addFields(embedBuilder, event);

		return embedBuilder.build();
	}

	/**
	 * Ensures that the url is an absolute uri
	 *
	 * @param url to check
	 * @return usable url
	 */
	private String fixUrl(String url) {
		//If the request was made from the discord the url is a relative URI, with absolute path
		//If an update is triggered by the website the url is an absolut URI
		//I wasn't able to find a fix for this other than this workaround :(
		if (!url.startsWith("http")) {
			return "https://armamachtbock.de" + url;
		}
		return url;
	}

	private static void addFields(@NonNull EmbedBuilder embedBuilder, @NonNull EventApiDto event) {
		addField("Zeitplan", buildScheduleField(event.getDate(), event.getStartTime(), event.getMissionLength()), embedBuilder);
		addField("Missionstyp", event.getMissionTypeAndRespawn(), true, embedBuilder);
		addField("Karte", event.getMap(), true, embedBuilder);
		addField("Modpack", buildModpackField(event.getModPack(), event.getModPackUrl()), true, embedBuilder);
		addField("Kann die Reserve mitspielen?", buildReserveParticipatingField(event.getReserveParticipating()), embedBuilder);
		addField("Missionszeit", event.getMissionTime(), true, embedBuilder);
		addField("Navigation", event.getNavigation(), true, embedBuilder);
		addField("Medicsystem", event.getMedicalSystem(), true, embedBuilder);
		addField("Technischer Teleport", event.getTechnicalTeleport(), true, embedBuilder);
	}

	private static String buildScheduleField(LocalDate eventDate, LocalTime eventStartTime, String missionLength) {
		final String dateTimeText = DATE_FORMATTER.format(eventDate) + ", " + TIME_FORMATTER.format(eventStartTime) + " Uhr";
		return StringUtils.isNotEmpty(missionLength) ? dateTimeText + " und dauert " + missionLength : dateTimeText;
	}

	private static String buildModpackField(String modPack, String modPackUrl) {
		return StringUtils.isNotEmpty(modPackUrl) ? "[" + modPack + "](" + fixUrl(modPackUrl) + ")" : modPack;
	}

	private static String buildReserveParticipatingField(Boolean reserveParticipating) {
		if (reserveParticipating == null) {
			return null;
		}
		return reserveParticipating ? "Ja" : "Nein";
	}
}
