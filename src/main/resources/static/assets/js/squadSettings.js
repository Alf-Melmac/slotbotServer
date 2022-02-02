$(function ($) {
	"use strict";

	const $squadSettings = $('#squadSettings');
	const squadSettingsModal = $squadSettings.find('.modal-body').prop('outerHTML');

	$squadSettings.on('show.bs.modal', function (e) {
		const $dropdownEl = $(e.relatedTarget);
		$dropdownEl.addClass('js-active-modal');
		const $modal = $(e.currentTarget);
		$modal.find('#squadSettingsModalLabel').text(getHeaderForSquadModal($dropdownEl.parents('.form-row')));

		//Strangely JQuery doesn't update data fields, therefor using attr
		const reservedFor = $dropdownEl.attr('data-reservedfor');
		if (reservedFor && reservedFor !== 'null') {
			$squadSettings.find(`#squadSettingsReservation option[value="${reservedFor}"]`).prop('selected', true);
		}
	});

	$squadSettings.on('click', '#saveSquadSettings', function (e) {
		saveSettings($, e);
		$squadSettings.modal('hide');
	});

	$squadSettings.on('hidden.bs.modal', function (e) {
		$('.js-active-modal').removeClass('js-active-modal');
		$squadSettings.find('.modal-body').replaceWith(squadSettingsModal);
	});
});

function saveSettings($, e) {
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
}

function getHeaderForSquadModal($row) {
	let header = 'Regeln für';
	const squadName = $row.find('.js-squad-name').val().trim();
	if (squadName) {
		header += ` ${squadName}`;
	} else {
		header += ' Squad';
	}
	return header;
}
