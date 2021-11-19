$(function () {
    "use strict";

    $.fn.editable.defaults.mode = 'inline';
    $.fn.editable.defaults.url = putUserEditableUrl;
    $.fn.editable.defaults.pk = user.id;
    $.fn.editable.defaults.success = showSavedToast;
    $.fn.editable.defaults.error = showErrorToast;

    $('#steamId').editable({
        inputclass: 'w-card hide-spin-button',
        value: user.steamId64,
        type: 'number',
        name: 'steamId64',
        emptytext: 'Steam 64 ID'
    });

    const $externalCalendarUrl = $('#externalCalendarUrl');
    $('#externalCalendarSwitch').on('change', function () {
        const integrationActive = $(this).is(':checked');
        if (integrationActive) {
            $externalCalendarUrl.show();
        } else {
            $externalCalendarUrl.hide();
        }
        $.ajax(putExternalCalendarIntegration.replace('{integrationActive}', integrationActive), {
                method: 'PUT'
            }
        )
            .done(showSavedToast)
            .fail(showErrorToast);
    });
});
