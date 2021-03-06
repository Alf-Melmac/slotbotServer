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
        lang: {
            next: 'Weiter',
            previous: 'Vorherige'
        }
    });

    // Step show event
    $smartWizard.on('showStep', function (e, anchorObject, stepNumber, stepDirection, stepPosition) {
        const $btnFinish = $('#btnFinish');

        $('#prev-btn').prop('disabled', stepPosition === 'first');

        let last = stepPosition === 'last';
        $('#next-btn').prop('disabled', last);
        $btnFinish.toggle(last);
    });

    // Step leave event
    $smartWizard.on('leaveStep', function(e, anchorObject, currentStepIndex, nextStepIndex, stepDirection) {
        if (stepDirection === 'forward') {
            return areAllRequiredFieldsFilled('[required]:visible');
        }
    });
});