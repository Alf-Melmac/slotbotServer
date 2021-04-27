let colorpicker;

$(function () {
    "use strict";
    $('#colorPicker').colorpicker({
        format: 'hex',
        useAlpha: false,
        color: Math.round(Math.random() * 0x1000000) //Random color as default
    })
        .on('colorpickerCreate', function (event) {
            colorpicker = event.colorpicker;
        });
});

function setColor(value) {
    if (!colorpicker) {
        console.error('Colorpicker not initialized');
    }
    colorpicker.setValue(value);
}