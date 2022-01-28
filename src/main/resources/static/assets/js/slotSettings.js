$(function ($) {
	"use strict";

	const $squads = $('#squads');

	const $slotSettings = $('#slotSettings');
	const slotSettingsModal = $slotSettings.find('.modal-body').prop('outerHTML');

	$slotSettings.on('show.bs.modal', function (e) {
		const $dropdownEl = $(e.relatedTarget);
		$dropdownEl.addClass('js-active-modal');
		const $modal = $(e.currentTarget);
		$modal.find('#slotSettingsModalLabel').text(getHeaderForModal($dropdownEl.parents('.form-row')));

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
		const $modalContent = $(e.currentTarget).parents('.modal-content');
		const $dropdownEl = $('.js-active-modal');
		$modalContent.find('select,input').each(function (index, element) {
			const $el = $(element);
			const key = $el.data('key');

			if (!key || key === '') {
				console.error('empty key');
				console.log($el);
				return;
			}

			let value = $el.val();
			if (typeof value == 'string') {
				value = value.trim();
			}
			$dropdownEl.attr(`data-${key}`, value);
		});
		$dropdownEl.attr('data-blocked', $modalContent.find('#slotSettingBlocked').find('.fa-lock').length !== 0);
		$slotSettings.modal('hide');
	})

	$slotSettings.on('hidden.bs.modal', function (e) {
		$('.js-active-modal').removeClass('js-active-modal');
		$slotSettings.find('.modal-body').replaceWith(slotSettingsModal);
	})

	$slotSettings.on('click', '#slotSettingBlocked', /*':not(.js-blocked, .btn-denied)',*/ function() {
		$(this).find('.fas').toggleClass('fa-lock-open fa-lock');
		$slotSettings.find('#slotReplacementText').toggle($(this).find('.fa-lock').length !== 0);
	});

	$squads.on('click', '.js-trash', function () {
		const $row = $(this).parents('.form-row');
		if ($row.hasClass('js-squad')) {
			$row.parent('.js-complete-squad').remove();
		} else {
			$row.remove();
		}
	});
});

function getHeaderForModal($row) {
	let header = 'Regeln für';
	const slotNumber = $row.find('.js-slot-number').val();
	if (!isNaN(slotNumber)) {
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
