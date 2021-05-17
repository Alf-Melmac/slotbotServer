$(function () {
    "use strict";

    $('.js-server-toggle').on('click', function () {
        const $this = $(this);
        $.ajax(
            serverToggleUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                data: $this.data('serverip')
            }
        );
    });
});
