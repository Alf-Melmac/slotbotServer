$(function () {
    "use strict";
    $('#colorPicker').colorpicker({
        format: 'hex',
        useAlpha: false,
        color: Math.round(Math.random() * 0x1000000) //Random color as default
    });
});