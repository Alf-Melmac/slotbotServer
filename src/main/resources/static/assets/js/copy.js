$(function () {
    "use strict";

    $('.copy-to-clipboard input').on('click', function () {
        const $this = $(this);
        $this.focus();
        $this.select();
        document.execCommand('copy');
        $('.copied').text('In Zwischenablage kopiert').show().fadeOut(1200);
    });
});
