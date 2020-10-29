$(function () {
    "use strict";

    const $smartWizard = $("#smartwizard");

    // Toolbar extra buttons
    const btnCancel = $('<button id="btnCancel" class="btn btn-danger">Abbrechen</button>')
        .on('click', function () {
            window.location.href = eventsUrl;
        });

    const btnFinish = $('<button id="btnFinish" class="btn btn-primary">Speichern</button>')
        .on('click', function () {
            // Manual leaveStepCheck
            if (!areAllRequiredFieldsFilled()) {
                return;
            }

            let event = {};
            $('input,textarea,select')
                .filter((index, element) => !$(element).attr('class').includes('squad') && !$(element).attr('class').includes('slot'))
                .each(function (index, element) {
                    const $el = $(element);
                    const key = $el.data('dtokey');

                    if (!key || key === '') {
                        console.error('empty key');
                        console.log($el);
                        return;
                    }

                    // Special treatment, because this is a compound field
                    if (key === 'missionType') {
                        const missionTypeVal = event[key];
                        if (!$el.is(':checkbox')) {
                            if (missionTypeVal) {
                                event[key] = $el.val() + missionTypeVal;
                                return;
                            }
                        } else {
                            const respawnText = $el.is(':checked') ? ', Respawn' : ', Kein Respawn';
                            event[key] = missionTypeVal ? missionTypeVal + respawnText : respawnText;
                            return;
                        }
                    }

                    let value = $el.val();
                    if ($el.is(":checkbox")) {
                        value = $el.is(':checked');
                    }
                    if (value !== '') {
                        event[key] = value;
                    }
                });

            let squads = [];
            $('#squads .js-complete-squad').each(function (index, element) {
                const $completeSquad = $(element);
                let squad = {
                    name: $completeSquad.find('.js-squad-name').val(),
                    slotList: []
                };

                $completeSquad.find('.js-slot').each(function (index, element) {
                    const $slot = $(element)
                    squad.slotList.push({
                        name: $slot.find('.js-slot-name').val(),
                        number: $slot.find('.js-slot-number').val()
                    });
                });

                squads.push(squad);
            });
            event.squadList = squads;

            $.ajax(postEventUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                data: JSON.stringify(event)
            })
                .done(event => alert(JSON.stringify(event)))
                .fail(response => alert(JSON.stringify(response) + '\nEvent Erstellung fehlgeschlagen. Später erneut versuchen\n' + JSON.stringify(event)));
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
    $smartWizard.on("leaveStep", areAllRequiredFieldsFilled);

    function areAllRequiredFieldsFilled() {
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

        return valid;
    }


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