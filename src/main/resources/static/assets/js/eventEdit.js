$(function () {
    "use strict";

    $('#btnCancel').on('click', () => window.location.href = eventDetailsUrl);

    $('#btnFinish').on('click', function () {
        saveEvent($(this), putEventUrl, 'PUT', true);
    });
});