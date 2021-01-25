$(function () {
    "use strict";

    $('.js-server-toggle').on('click', function () {
        const $this = $(this);
        $.ajax(
            serverToggleUrl.replace('{online}', $this.data('online')), {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                data: $this.data('serverip')
            }
        );
    });
});