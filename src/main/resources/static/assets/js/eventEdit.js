$(function () {
    "use strict";

    setIndeterminateIfRequired('respawn');
    setIndeterminateIfRequired('reserveParticipating');

    $('#btnCancel').on('click', () => window.location.href = eventDetailsUrl);

    $('#btnFinish').on('click', function () {
        saveEvent($(this), putEventUrl, 'PUT', true);
    });
});

function setIndeterminateIfRequired(dtoKey) {
    if (savedEvent[dtoKey] === null) {
        $(`:checkbox[data-dtokey='${dtoKey}']`).prop('indeterminate', true);
    }
}