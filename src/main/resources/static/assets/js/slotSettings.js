$(function ($) {
	"use strict";

	const $slotSettings = $('#slotSettings');
	const slotSettingsModal = $slotSettings.find('.modal-body').prop('outerHTML');

	$slotSettings.on('show.bs.modal', function (e) {
		const $dropdownEl = $(e.relatedTarget);
		$dropdownEl.addClass('js-active-modal');
		const $modal = $(e.currentTarget);
		$modal.find('#slotSettingsModalLabel').text(getHeaderForSlotModal($dropdownEl.parents('.form-row')));

		//Strangely JQuery doesn't update data fields, therefor using attr
		const reservedFor = $dropdownEl.attr('data-reservedfor');
		if (reservedFor && reservedFor !== 'null') {
			$slotSettings.find(`#slotSettingReservation option[value="${reservedFor}"]`).prop('selected', true);
		}
		if ($dropdownEl.attr('data-blocked') === 'true') { //Bang Bang doesn't work?!
			$slotSettings.find('#slotSettingBlocked').trigger('click');
		}
		$slotSettings.find('#slotReplacementText').val($dropdownEl.attr('data-replacementtext'));
	});

	$slotSettings.on('click', '#saveSlotSettings', function (e) {
		saveSettings($, e);
		const $dropdownEl = $('.js-active-modal');
		const $modalContent = $(e.currentTarget).parents('.modal-content');
		$dropdownEl.attr('data-blocked', $modalContent.find('#slotSettingBlocked').find('.fa-lock').length !== 0);
		$slotSettings.modal('hide');
	});

	$slotSettings.on('hidden.bs.modal', function (e) {
		$('.js-active-modal').removeClass('js-active-modal');
		$slotSettings.find('.modal-body').replaceWith(slotSettingsModal);
	});

	$slotSettings.on('click', '#slotSettingBlocked', /*':not(.js-blocked, .btn-denied)',*/ function() {
		$(this).find('.fas').toggleClass('fa-lock-open fa-lock');
		$slotSettings.find('#slotReplacementText').toggle($(this).find('.fa-lock').length !== 0);
	});
});

function getHeaderForSlotModal($row) {
	let header = 'Regeln für';
	const slotNumber = $row.find('.js-slot-number').val();
	if (!Number.isNaN(slotNumber)) {
		header += ` (${slotNumber})`;
	}
	const slotName = $row.find('.js-slot-name').val().trim();
	if (slotName) {
		header += ` ${slotName}`;
	} else {
		header += ' Slot';
	}
	return header;
}
