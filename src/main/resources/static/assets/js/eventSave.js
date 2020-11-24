function areAllRequiredFieldsFilled(selector) {
    let valid = true;
    $('input,textarea,select').filter(selector).each(function (index, element) {
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

function saveEvent($saveBtn, url, ajaxMethod, update) {
    // Disable Finish button to prevent spamming
    $saveBtn.prop('disabled', true);

    if (!areAllRequiredFieldsFilled('[required]')) {
        $saveBtn.popover('show');
        $saveBtn.prop('disabled', false);
        return;
    } else {
        $saveBtn.popover('hide');
    }

    if (!checkUniqueSlotNumbers()) {
        $('#uniqueSlotNumbersError').show().fadeOut(5000);
        $saveBtn.prop('disabled', false);
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

            let value = $el.val();
            const isCheckbox = $el.is(':checkbox');
            if (isCheckbox) {
                if ($el.is(':indeterminate')) {
                    return;
                }
                value = $el.is(':checked');
            } else if (!value) {
                return;
            }
            if (value !== '') {
                event[key] = value;
            }
        });

    let squads = [];
    $('#squads .js-complete-squad').each(function (completeSquadIndex, completeSquadElement) {
        const $completeSquad = $(completeSquadElement);
        let squad = {
            name: $completeSquad.find('.js-squad-name').val(),
            slotList: []
        };
        if (update) {
            squad.id = $completeSquad.data('squadid');
        }

        $completeSquad.find('.js-slot').each(function (slotIndex, slotElement) {
            const $slot = $(slotElement);
            let slot = {
                name: $slot.find('.js-slot-name').val(),
                number: $slot.find('.js-slot-number').val()
            };
            if (update) {
                slot.id = $slot.data('slotid');
            }
            squad.slotList.push(slot);
        });

        squads.push(squad);
    });
    event.squadList = squads;

    event.hidden = $('#eventHidden').find('.fa').hasClass('fa-eye-slash');

    $.ajax(url, {
        method: ajaxMethod,
        headers: {
            'Content-Type': 'application/json'
        },
        data: JSON.stringify(event)
    })
        .done(savedEvent => window.location.href = eventDetailsUrl.replace('{eventId}', savedEvent.id))
        .fail(response => alert(JSON.stringify(response) + '\nAktion fehlgeschlagen. Später erneut versuchen\n' + JSON.stringify(event)));
}