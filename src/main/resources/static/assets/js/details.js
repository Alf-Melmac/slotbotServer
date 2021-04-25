$(function () {
    "use strict";

    const $eventDetails = $('#eventDetails');
    const $addField = $('#addField');
    const $fieldCounter = $('#fieldCounter');
    const $fieldCounterCount = $('#fieldCounterCount');

    const newField =
        '<div class="form-row align-items-center js-field">' +
        '   <div><em class="fas fa-arrows-alt-v"></em></div>' +
        '   <div class="col-md-4">' +
        '      <input class="form-control js-field-title" type="text" placeholder="Titel" required>' +
        '   </div>' +
        '   <div class="col-md-7">' +
        '      <textarea class="form-control one-row js-field-text" type="text" placeholder="Information" required></textarea>' +
        '   </div>' +
        '   <div class="d-flex btn btn-xl btn-wht js-trash" type="button"><em class="far fa-trash-alt"></em></div>' +
        '</div>';

    $addField.on('click', function () {
        $eventDetails.append(newField);
        calculateFieldCounter($eventDetails, $addField, $fieldCounter, $fieldCounterCount);
    });

    $eventDetails.on('click', '.js-trash', function () {
        const $row = $(this).parent('.form-row');
        $row.remove();
        calculateFieldCounter($eventDetails, $addField, $fieldCounter, $fieldCounterCount);
    });
});

function calculateFieldCounter($eventDetails, $addField, $fieldCounter, $fieldCounterCount) {
    const count = $eventDetails.find('.js-field').length;

    $fieldCounterCount.text(count);

    if (count >= 0 && count < 20) {
        $fieldCounter.attr('class', 'bg-success p-1');
    } else if (count >= 20 && count < 23) {
        $fieldCounter.attr('class', 'bg-warning p-1');
    } else {
        $fieldCounter.attr('class', 'bg-danger p-1');
        $addField.prop('disabled', true);
        return;
    }
    $addField.prop('disabled', false);
}