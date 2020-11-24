$(function () {
    "use strict";

    $('#eventHidden').on('click', function () {
        $(this).find('.fa').toggleClass('fa-eye fa-eye-slash');
    });
});