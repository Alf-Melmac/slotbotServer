$(function () {
    "use strict";

    const $smartWizard = $("#smartwizard");

    // Toolbar extra buttons
    const btnCancel = $('<button id="btnCancel" class="btn btn-danger">Abbrechen</button>')
        .on('click', function () {
            $('#smartwizard').smartWizard("reset");
        });
    const btnFinish = $('<button id="btnFinish" class="btn btn-primary">Speichern</button>')
        .on('click', function () {
            alert('Finish Clicked');
        });

    $smartWizard.smartWizard({
        theme: 'dots',
        darkMode: true,
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
    $smartWizard.on("showStep", function (e, anchorObject, stepNumber, stepDirection, stepPosition) {
        const $btnFinish = $('#btnFinish');

        $("#prev-btn").prop('disabled', stepPosition === 'first');

        let last = stepPosition === 'last';
        $("#next-btn").prop('disabled', last);
        $btnFinish.toggle(last);
    });

    // Step leave event
    $smartWizard.on("leaveStep", function (e, anchorObject, stepNumber, stepDirection, stepPosition) {
        let valid = true;
        $('input,textarea,select').filter('[required]:visible').each(function (index, element) {
            const $el = $(element);
            const value = $el.val();
            if (!value || value === '') {
                $el.addClass('is-invalid');
                $el.on('change', (event) => $(event.target).removeClass('is-invalid'));
                valid = false;
            }
        });

        // Prevent page switch if inputs aren't valid
        return valid;
    });


    // Date and time picker
    $('#datepicker').datetimepicker({
        format: 'yyyy-MM-DD',
        locale: 'de',
        minDate: moment()
    });

    $('#datetime').datetimepicker({
        format: 'LT',
        locale: 'de'
    });
});