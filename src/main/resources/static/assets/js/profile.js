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
});
