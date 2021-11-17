package de.webalf.slotbot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.webalf.slotbot.util.bot.MentionUtils;
import lombok.*;

import javax.persistence.*;

/**
 * @author Alf
 * @since 19.06.2021
 */
@Entity
@Table(name = "event_discord_information", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"discord_information_channel"}), @UniqueConstraint(columnNames = {"discord_information_info_msg"}),
		@UniqueConstraint(columnNames = {"discord_information_slotlist_msg_one"}), @UniqueConstraint(columnNames = {"discord_information_slotlist_msg_two"})})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class EventDiscordInformation {
	@Id
	@Column(name = "discord_information_channel", unique = true, nullable = false, updatable = false)
	private long channel;

	@Column(name = "discord_information_guild", nullable = false, updatable = false)
	private long guild;

	@Column(name = "discord_information_info_msg", unique = true)
	private Long infoMsg;

	@Column(name = "discord_information_slotlist_msg_one", unique = true)
	private Long slotListMsgPartOne;

	@Column(name = "discord_information_slotlist_msg_two", unique = true)
	private Long slotListMsgPartTwo;

	@ManyToOne(targetEntity = Event.class, optional = false)
	@JoinColumn(name = "event_id")
	@JsonBackReference
	private Event event;

	public String getChannelAsMention() {
		return MentionUtils.getChannelAsMention(getChannel());
	}
}