$(function () {
    "use strict";
    // Date and time picker
    $('#datepicker').datetimepicker({
        format: 'yyyy-MM-DD',
        locale: 'de',
        minDate: moment()
    });

    $('#datetime').datetimepicker({
        format: 'LT',
        locale: 'de'
    });
});