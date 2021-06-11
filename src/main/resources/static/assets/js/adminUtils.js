$(function ($) {
    "use strict";

    $('button').on('click', function () {
        const $this = $(this);
        $.ajax(
            postActionUrl.replace('{action}', $this.val()), {
                method: 'POST'
            }
        );
    });
});
