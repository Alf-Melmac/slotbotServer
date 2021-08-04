let $spinner;

$(function ($) {
    "use strict";
    $spinner = $('#spinner');
    $spinner.hide();

    $(window).on('beforeunload', function(){
        $spinner.hide();
    });
});

function showSpinner() {
    $spinner.show();
}
