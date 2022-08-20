package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.assembler.GuildAssembler;
import de.webalf.slotbot.assembler.website.EventDetailsAssembler;
import de.webalf.slotbot.controller.EventController;
import de.webalf.slotbot.controller.FileController;
import de.webalf.slotbot.model.Event;
import de.webalf.slotbot.service.EventService;
import de.webalf.slotbot.service.EventTypeService;
import de.webalf.slotbot.service.GuildService;
import de.webalf.slotbot.util.LongUtils;
import de.webalf.slotbot.util.StringUtils;
import de.webalf.slotbot.util.bot.DiscordUserUtils;
import de.webalf.slotbot.util.permissions.PermissionChecker;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import static de.webalf.slotbot.util.permissions.ApplicationPermissionHelper.HAS_POTENTIALLY_ROLE_EVENT_MANAGE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author Alf
 * @since 24.10.2020
 */
@Controller
@RequestMapping("/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventWebController {
	private final PermissionChecker permissionChecker;
	private final EventService eventService;
	private final EventDetailsAssembler eventDetailsAssembler;
	private final EventTypeService eventTypeService;
	private final GuildService guildService;

	private static final String EVENTS_URL_STRING = "eventsUrl";
	private static final String EVENTS_URL = linkTo(methodOn(EventWebController.class).getWizardHtml(null, null)).toUri().toString();

	@GetMapping("/new")
	@PreAuthorize("@permissionChecker.hasEventManagePermissionInCurrentOwnerGuild()")
	@Deprecated
	public ModelAndView getWizardHtml(@RequestParam(required = false) String date, @RequestParam(required = false) String copyEvent) {
		ModelAndView mav = new ModelAndView("eventWizard");

		addCalendarSubPageObjects(mav);
		addEventManageObjects(mav);
		mav.addObject(EVENTS_URL_STRING, EVENTS_URL);
		mav.addObject("date", date);
		if (StringUtils.isNotEmpty(copyEvent) && StringUtils.onlyNumbers(copyEvent)) {
			eventService.findOptionalById(Integer.parseInt(copyEvent))
					.ifPresent(event -> mav.addObject("copyEvent", eventDetailsAssembler.toDto(event, false)));
		}
		mav.addObject("uploadSqmFileUrl", linkTo(methodOn(FileController.class).postSqmFile(null)).toUri().toString());
		mav.addObject("postEventUrl", linkTo(methodOn(EventController.class).oldPostEvent(null)).toUri().toString());
		mav.addObject("eventDetailsUrl", linkTo(methodOn(EventController.class)
				.getEventDetails(Long.MIN_VALUE))
				.toUri().toString()
				.replace(LongUtils.toString(Long.MIN_VALUE), "{eventId}"));
		return mav;
	}

	@GetMapping("/{id}/edit/old")
	@PreAuthorize(HAS_POTENTIALLY_ROLE_EVENT_MANAGE)
	public ModelAndView getEventEditHtml(@PathVariable(value = "id") long eventId) {
		final Event event = eventService.findById(eventId);
		permissionChecker.assertEventManagePermission(event.getOwnerGuild());

		ModelAndView mav = new ModelAndView("eventEdit");

		addCalendarSubPageObjects(mav);
		addEventManageObjects(mav);
		mav.addObject(EVENTS_URL_STRING, EVENTS_URL);
		mav.addObject("event", eventDetailsAssembler.toEditDto(event));
		mav.addObject("canRevokeShareable", event.canRevokeShareable());
		final boolean canUploadSlotlist = event.isEmpty();
		mav.addObject("canUploadSlotlist", canUploadSlotlist);
		if (canUploadSlotlist) {
			mav.addObject("uploadSqmFileUrl", linkTo(methodOn(FileController.class).postSqmFile(null)).toUri().toString());
		}
		mav.addObject("putEventEditableUrl", linkTo(methodOn(EventController.class).updateEventEditable(eventId, null, null)).toUri().toString());
		mav.addObject("eventDetailsUrl", linkTo(methodOn(EventController.class).getEventDetails(eventId)).toUri().toString());
		mav.addObject("putEventUrl", linkTo(methodOn(EventController.class).updateEvent(eventId, null)).toUri().toString());
		mav.addObject("putSlotListUrl", linkTo(methodOn(EventController.class).updateSlotList(eventId, null)).toUri().toString());
		return mav;
	}

	private void addCalendarSubPageObjects(@NonNull ModelAndView mav) {
		mav.addObject("loginUrl", "/oauth2/authorization/discord");
		final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof OAuth2User) {
			OAuth2User oAuth2User = (OAuth2User) principal;
			mav.addObject("avatarUrl", DiscordUserUtils.getAvatarUrl(oAuth2User.getAttribute("id"), oAuth2User.getAttribute("avatar"), oAuth2User.getAttribute("discriminator")));
			mav.addObject("profileUrl", "/profile/" + oAuth2User.getAttribute("id"));
		}
	}

	private void addEventManageObjects(@NonNull ModelAndView mav) {
		mav.addObject("eventTypes", eventTypeService.findAll());
		mav.addObject("eventFieldDefaultsUrl", linkTo(methodOn(EventController.class).getEventFieldDefaults(null)).toUri().toString());
		mav.addObject("guilds", GuildAssembler.toDtoList(guildService.findAllExceptDefault()));
	}
}
