$(function () {
    "use strict";

    $('.copy-to-clipboard input').on('click', function () {
        const $this = $(this);
        $this.focus();
        $this.select();
        document.execCommand('copy');
        $('.copied').text('Copied to clipboard').show().fadeOut(1200);
    });

    $('#eventEdit').on('click', () => window.location.href = eventEditUrl);
});
