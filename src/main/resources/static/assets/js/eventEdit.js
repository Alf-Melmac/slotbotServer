$(function () {
    "use strict";

    setIndeterminateIfRequired('respawn');
    setIndeterminateIfRequired('reserveParticipating');

    $.fn.editable.defaults.mode = 'inline';
    $.fn.editable.defaults.url = putEventEditableUrl;
    $.fn.editable.defaults.pk = savedEvent.id;
    $.fn.editable.defaults.success = showSavedToast;
    $.fn.editable.defaults.error = showErrorToast;

    $('#eventName').editable({
        inputclass: 'w-11',
        value: savedEvent.name,
        type: 'text',
        name: 'name',
        validate: requiredField
    });

    $('#eventCreator').editable({
        inputclass: 'w-4',
        value: savedEvent.creator,
        type: 'text',
        name: 'creator',
        validate: requiredField
    });

    $('#eventDescription').editable({
        inputclass: 'w-12',
        value: savedEvent.description,
        type: 'textarea',
        name: 'description',
        emptytext: 'Beschreibung'
    });

    $('#eventMissionLength').editable({
        inputclass: 'w-4',
        value: savedEvent.missionLength,
        type: 'text',
        name: 'missionLength',
        tpl: '<input class="form-control custom-select" type="text" list="missionLengths">',
        emptytext: 'Nicht angegeben'
    });

    $('#eventModPack').editable({
        inputclass: 'w-4',
        value: savedEvent.modPack,
        type: 'text',
        name: 'modPack',
        tpl: '<input class="form-control custom-select" type="text" list="modPacks">',
        emptytext: 'Kein Modpack ausgewählt'
    });

    $('#eventTechnicalTeleport').editable({
        inputclass: 'w-4',
        value: savedEvent.technicalTeleport,
        type: 'text',
        name: 'technicalTeleport',
        emptytext: 'Objekt, Position etc.'
    });

    $('#eventMedicalSystem').editable({
        inputclass: 'w-4',
        value: savedEvent.medicalSystem,
        type: 'text',
        name: 'medicalSystem'
    });

    $('#eventMissionTime').editable({
        inputclass: 'w-4',
        value: savedEvent.missionTime,
        type: 'text',
        name: 'missionTime',
        emptytext: 'Dämmerung, Tag, Nacht, ...'
    });

    $('#eventNavigation').editable({
        inputclass: 'w-4',
        value: savedEvent.navigation,
        type: 'text',
        name: 'navigation',
        emptytext: 'GPS, Karte, Kompass, Nichts, ...'
    });

    $('#eventPicture').editable({
        inputclass: 'w-8',
        value: savedEvent.pictureUrl,
        type: 'text',
        name: 'pictureUrl',
        emptytext: 'Kein Bild'
    });

    //Event hidden button
    $('#eventHidden').on('click', function () {
        const $icon = $(this).find('.far');
        putUpdate({hidden: !$icon.hasClass('fa-eye-slash')}, () => {
            $icon.toggleClass('fa-eye fa-eye-slash');
            showSavedToast();
        });
    });

    //Selects
    $('select').on('change', function () {
        const $this = $(this);
        putUpdate({[$this.data('dtokey')]: $this.val()}, showSavedToast);
    });

    //Datetimepickers
    $('#datepicker, #datetime').on('change.datetimepicker', function () {
        const $thisInput = $(this).find('input');
        putUpdate({[$thisInput.data('dtokey')]: $thisInput.val()}, showSavedToast);
    });

    //Checkboxes
    $(':checkbox').on('change', function () {
        const $this = $(this);
        putUpdate({[$this.data('dtokey')]: $this.is(':checked')}, showSavedToast);
    });

    $('#btnSaveSlotlist').on('click', function () {
        if (validateRequiredAndUnique($(this))) {
            return;
        }

        putUpdate({squadList: getSquads(true)}, showSavedToast);
    });
});

function setIndeterminateIfRequired(dtoKey) {
    if (savedEvent[dtoKey] === null) {
        $(`:checkbox[data-dtokey='${dtoKey}']`).prop('indeterminate', true);
    }
}

function requiredField(value) {
    const trimmedVal = $.trim(value);
    if (!trimmedVal || trimmedVal === '') {
        return 'Pflichtfeld!';
    }
}

function showSavedToast() {
    $('#savedToast').toast('show');
}

function showErrorToast() {
    $('#errorToast').toast('show');
}

function putUpdate(data, callback) {
    $.ajax(putEventUrl, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        data: JSON.stringify(data)
    })
        .done(callback)
        .fail(showErrorToast);
}