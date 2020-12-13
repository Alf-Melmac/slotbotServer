$(function () {
    "use strict";
    // Date and time picker
    $('#datepicker').datetimepicker({
        format: 'yyyy-MM-DD',
        locale: 'de',
        minDate: moment().subtract(1, 'days'),
        allowInputToggle: true
    });

    $('#datetime').datetimepicker({
        format: 'LT',
        locale: 'de',
        allowInputToggle: true
    });
});