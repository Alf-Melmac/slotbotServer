package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.webalf.slotbot.converter.persistence.LocalDateTimePersistenceConverter;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Alf
 * @since 22.06.2020
 */
@Entity
@Table(name = "event_old", uniqueConstraints = {@UniqueConstraint(columnNames = {"id", "event_channel"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class OldEvent extends AbstractIdEntity {
	@Column(name = "event_name", length = 100)
	@NotBlank
	@Size(max = 80)
	private String name;

	@Column(name = "event_date")
	@NotNull
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	private LocalDateTime dateTime;

	@Column(name = "event_creator")
	@NotBlank
	@Size(max = 80)
	private String creator;

	@Column(name = "event_hidden")
	private boolean hidden;

	@Column(name = "event_channel")
	private Long channel;

	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderColumn
	@JsonManagedReference
	private List<Squad> squadList;

	@Column(name = "event_info_msg")
	private Long infoMsg;

	@Column(name = "event_slotlist_msg")
	private Long slotListMsg;

	@Column(name = "event_description", length = (int) (MessageEmbed.TEXT_MAX_LENGTH * 1.25))
	@Size(max = MessageEmbed.TEXT_MAX_LENGTH)
	private String description;

	@Column(name = "event_picture_url", length = 2083)
	@Size(max = 1666)
	private String pictureUrl;

	@Column(name = "event_mission_type", length = 100)
	@Size(max = 80)
	private String missionType;

	@Column(name = "event_respawn")
	private Boolean respawn;

	@Column(name = "event_mission_length", length = 100)
	@Size(max = 80)
	private String missionLength;

	@Column(name = "event_reserve_participating")
	private Boolean reserveParticipating;

	@Column(name = "event_mod_pack", length = 100)
	@Size(max = 80)
	private String modPack;

	@Column(name = "event_map", length = 100)
	@Size(max = 80)
	private String map;

	@Column(name = "event_mission_time", length = 100)
	@Size(max = 80)
	private String missionTime;

	@Column(name = "event_navigation", length = 100)
	@Size(max = 80)
	private String navigation;

	@Column(name = "event_technical_teleport", length = 100)
	@Size(max = 80)
	private String technicalTeleport;

	@Column(name = "event_medical_system", length = 100)
	@Size(max = 80)
	private String medicalSystem;

	@Builder
	public OldEvent(long id,
					String name,
					LocalDateTime dateTime,
					String creator,
					Boolean hidden,
					Long channel,
					List<Squad> squadList,
					Long infoMsg,
					Long slotListMsg,
					String description,
					String pictureUrl,
					String missionType,
					Boolean respawn,
					String missionLength,
					Boolean reserveParticipating,
					String modPack,
					String map,
					String missionTime,
					String navigation,
					String technicalTeleport,
					String medicalSystem) {
		this.id = id;
		this.name = name;
		this.dateTime = dateTime;
		this.creator = creator;
		this.hidden = hidden != null && hidden;
		this.channel = channel;
		this.squadList = squadList;
		this.infoMsg = infoMsg;
		this.slotListMsg = slotListMsg;

		this.description = description;
		this.pictureUrl = pictureUrl;
		this.missionType = missionType;
		this.respawn = respawn;
		this.missionLength = missionLength;
		this.reserveParticipating = reserveParticipating;
		this.modPack = modPack;
		this.map = map;
		this.missionTime = missionTime;
		this.navigation = navigation;
		this.technicalTeleport = technicalTeleport;
		this.medicalSystem = medicalSystem;
	}
}
