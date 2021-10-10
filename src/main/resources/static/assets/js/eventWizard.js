$(function () {
    "use strict";

    $(':checkbox').prop('indeterminate', true);

    $('#eventHidden').on('click', function () {
        $(this).find('.far').toggleClass('fa-eye fa-eye-slash');
    });

    const $smartWizard = $('#smartwizard');

    // Toolbar extra buttons
    const btnCancel = $('<button id="btnCancel" class="btn btn-danger">Abbrechen</button>')
        .on('click', function () {
            window.location.href = eventsUrl;
        });

    const btnFinish = $('<button id="btnFinish" class="btn btn-primary" tabindex="0" data-toggle="popover"' +
        ' data-trigger="focus" data-content="Alle Pflichtfelder ausfüllen!">Speichern</button>')
        .on('click', function () {
            saveEvent($(this));
        });

    $smartWizard.smartWizard({
        theme: 'dots',
        darkMode: true,
        enableURLhash: false,
        transition: {
            animation: 'fade',
            speed: '400'
        },
        toolbarSettings: {
            toolbarPosition: 'bottom',
            toolbarExtraButtons: [btnCancel, btnFinish]
        },
        keyboardSettings: {
            keyNavigation: false,
        },
        lang: {
            next: 'Weiter',
            previous: 'Vorherige'
        }
    });

    // Step show event
    const $btnFinish = $('#btnFinish');
    const $prevBtn = $('#prev-btn');
    const $nextBtn = $('#next-btn');
    $smartWizard.on('showStep', function (e, anchorObject, stepNumber, stepDirection, stepPosition) {
        $prevBtn.prop('disabled', stepPosition === 'first');

        const last = stepPosition === 'last';
        $nextBtn.prop('disabled', last);
        $btnFinish.toggle(last);
    });

    // Step leave event
    $smartWizard.on('leaveStep', function (e, anchorObject, currentStepIndex, nextStepIndex, stepDirection) {
        if (stepDirection === 'forward') {
            return areAllRequiredFieldsFilled('[required]:visible');
        }
    });

    if (copyEvent) {
        for (const [key, value] of Object.entries(copyEvent)) {
            if (key === 'eventType') {
                Object.entries(value).forEach(([childKey, childValue]) => setValue(`${key}.${childKey}`, childValue));
                continue;
            } else if (key === 'hidden') {
                if (value) {
                    $('#eventHidden').trigger('click');
                }
                continue;
            }
            setValue(key, value);
        }
    }
});

const skipDtoKeys = ['date', 'channel', 'infoMsg', 'slotListMsg', 'pictureUrl', 'missionTypesFiltered'];
function setValue(dtoKey, value) {
    if (dtoKey.endsWith('id') || skipDtoKeys.includes(dtoKey)) {
        return;
    } else if (dtoKey === 'rawPictureUrl') {
        dtoKey = 'pictureUrl';
    } else if (dtoKey === 'squadList') {
        return addSlotList(value);
    } else if (dtoKey === 'details') {
        return addFields(value);
    } else if (dtoKey === 'reserveParticipating') {
        if (typeof value == 'boolean') {
            const $el = $(`[data-dtokey='${dtoKey}']`);
            $el.prop('indeterminate', false);
            $el.prop('checked', value);
            if (value) {
                $el.addClass('active');
            }
        }
        return;
    }
    $(`[data-dtokey='${dtoKey}']`).val(value);
}
