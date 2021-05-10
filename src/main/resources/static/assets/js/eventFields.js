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

    const $addDefaultFields = $('#addDefaultFields');
    $addDefaultFields.hide();

    $('#eventTypeName').on('input', function () {
        const $input = $(this);
        //Check if option is in datalist
        const match = $('#' + $input.attr('list') + ' option').filter(function () {
            return ($(this).val() === $input.val());
        });

        if (match.length > 0) {
            const $match = $(match[0]);
            setColor($match.data('color'));
            setFieldDefaults($match.val(), defaultFields);
        } else {
            $addDefaultFields.fadeOut();
        }
    });

    let defaultFields;

    function setFieldDefaults(eventType) {
        $.ajax(eventFieldDefaultsUrl, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            data: eventType
        })
            .done(defaults => {
                $addDefaultFields.text(eventType + ' default einfügen');
                defaultFields = defaults;
                if (!$.isEmptyObject(defaultFields)) {
                    $addDefaultFields.fadeIn();
                }
            })
    }

    $('#addDefaultFieldsConfirmed').on('click', function () {
        $('#addDefaultFieldsWarning').modal('hide');
        if (!defaultFields) {
            return;
        }
        $('.js-field').remove();
        addFields($addField, defaultFields);
    });
});

function addFields($addField, fields, update = false) {
    for (const field of fields) {
        $addField.trigger('click');
        fillField($('.js-field').last(), field, update);
        /*if (update) {
            $('.js-field').last().data('fieldid', field.id);
        }*/
    }
}

function fillField($field, field, update) {
    $field.find('.js-field-title').val(field.title);
    switch (field.type) {
        case 'TEXT':
            setInput($field, text(field));
            break;
        case 'TEXT_WITH_SELECTION':
            setInput($field, textWithSelection(field));
            break;
        case 'BOOLEAN':
            setInput($field, boolean(field));
            break;
        case 'SELECTION':
            setInput($field, selection(field));
            break;
        default:
            break;
    }
    if (update) {
        $field.attr('data-fieldid', field.id);
    }
}

function setSelectionId(html, title) {
    return html.replaceAll('{selectionId}', title);
}

function setText(html, text) {
    return html.replaceAll('{text}', text ? text : '');
}

function selectionInput(input, field) {
    const s = setSelectionId(input, field.title);
    let options = '';
    for (const selection of field.selection) {
        options += `<option>${selection}</option>`;
    }
    return s.replace('{options}', options);
}

const text_ =
    '<textarea id="{selectionId}" class="form-control one-row" type="text" placeholder="Information" required>{text}</textarea>';

function text(field) {
    return setText(setSelectionId(text_, field.title, field.text), field.text);
}

const text_with_selection =
    '<input id="{selectionId}" class="form-control custom-select" type="text" list="{selectionId}List" required value="{text}">' +
    '<datalist id="{selectionId}List">' +
    '   {options}' +
    '</datalist>';

function textWithSelection(field) {
    return setText(selectionInput(text_with_selection, field), field.text);
}

const boolean_ =
    '<div class="custom-control custom-checkbox">' +
    '   <input id="{selectionId}" class="custom-control-input" type="checkbox" required>' +
    '   <label for="{selectionId}" class="custom-control-label"></label>' +
    '</div>';

function boolean(field) {
    return setSelectionId(boolean_, field.title);
}

const selection_ =
    '<select id="{selectionId}" class="form-control custom-select" required>' +
    '   <option selected disabled>Auswählen...</option>' +
    '   {options}' +
    '</select>';

const selection_with_text =
    '<select id="{selectionId}" class="form-control custom-select" required>' +
    '   <option selected>{text}</option>' +
    '   {options}' +
    '</select>';

function selection(field) {
    if (field.text) {
        return setText(selectionInput(selection_with_text, field), field.text);
    }
    return selectionInput(selection_, field);
}

function setInput($field, html) {
    $field.find('.js-field-text').replaceWith(html);
}

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