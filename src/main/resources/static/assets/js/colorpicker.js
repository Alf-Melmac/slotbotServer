let colorpicker;

$(function () {
    "use strict";
    const $colorPicker = $('#colorPicker');
    $colorPicker.colorpicker({
        format: 'hex',
        useAlpha: false,
        color: initColor($colorPicker)
    })
        .on('colorpickerCreate', function (event) {
            colorpicker = event.colorpicker;
        });
});

function initColor($colorPicker) {
    const color = $colorPicker.find('[data-target="#colorPicker"]').val();
    return color ? color : Math.round(Math.random() * 0x1000000); //Random color if no initial color is specified
}

function setColor(value) {
    if (!colorpicker) {
        return;
    }
    colorpicker.setValue(value);
}
