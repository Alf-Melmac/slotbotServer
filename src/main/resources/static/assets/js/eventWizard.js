$(function () {
    "use strict";

    $(':checkbox').prop('indeterminate', true);

    $('#eventHidden').on('click', function () {
        $(this).find('.far').toggleClass('fa-eye fa-eye-slash');
    });

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
        }
    });

    let defaultFields;
    const $addDefaultFields = $('#addDefaultFields');

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
                $addDefaultFields.show();
                defaultFields = defaults;
            })
    }

    const $addField = $('#addField');
    $('#addDefaultFieldsConfirmed').on('click', function () {
        $('#addDefaultFieldsWarning').modal('hide');
        if (!defaultFields) {
            return;
        }
        $('.js-field').remove();
        for (const defaultField of defaultFields) {
            $addField.trigger('click');
            fillField($('.js-field').last(), defaultField);
        }
    });

    const $smartWizard = $('#smartwizard');

    // Toolbar extra buttons
    const btnCancel = $('<button id="btnCancel" class="btn btn-danger">Abbrechen</button>')
        .on('click', function () {
            window.location.href = eventsUrl;
        });

    const btnFinish = $('<button id="btnFinish" class="btn btn-primary" tabindex="0" data-toggle="popover"' +
        ' data-trigger="focus" data-content="Alle Pflichtfelder ausfüllen!">Speichern</button>')
        .on('click', function () {
            saveEvent($(this));
        });

    $smartWizard.smartWizard({
        theme: 'dots',
        darkMode: true,
        enableURLhash: false,
        transition: {
            animation: 'fade',
            speed: '400'
        },
        toolbarSettings: {
            toolbarPosition: 'bottom',
            toolbarExtraButtons: [btnCancel, btnFinish]
        },
        keyboardSettings: {
            keyNavigation: false,
        },
        lang: {
            next: 'Weiter',
            previous: 'Vorherige'
        }
    });

    // Step show event
    const $btnFinish = $('#btnFinish');
    const $prevBtn = $('#prev-btn');
    const $nextBtn = $('#next-btn');
    $smartWizard.on('showStep', function (e, anchorObject, stepNumber, stepDirection, stepPosition) {
        $prevBtn.prop('disabled', stepPosition === 'first');

        let last = stepPosition === 'last';
        $nextBtn.prop('disabled', last);
        $btnFinish.toggle(last);
    });

    // Step leave event
    $smartWizard.on('leaveStep', function (e, anchorObject, currentStepIndex, nextStepIndex, stepDirection) {
        if (stepDirection === 'forward') {
            return areAllRequiredFieldsFilled('[required]:visible');
        }
    });
});

function fillField($field, field) {
    $field.find('.js-field-title').val(field.title);
    switch (field.type) {
        case 'TEXT_WITH_SELECTION':
            setInput($field, textWithSelection(field));
            break;
        case 'BOOLEAN':
            setInput($field, boolean(field));
            break;
        case 'SELECTION':
            setInput($field, selection(field));
            break;
        case 'TEXT':
        default:
            break;
    }
}

function setSelectionId(html, title) {
    return html.replaceAll('{selectionId}', title);
}

function selectionInput(input, field) {
    const s = setSelectionId(input, field.title);
    let options = '';
    for (const selection of field.selection) {
        options += `<option>${selection}</option>`;
    }
    return s.replace('{options}', options);
}

const text_with_selection = 
    '<input id="{selectionId}" class="form-control custom-select" type="text" list="{selectionId}List" required>' +
    '<datalist id="{selectionId}List">' +
    '   {options}' +
    '</datalist>';

function textWithSelection(field) {
    return selectionInput(text_with_selection, field);
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

function selection(field) {
    return selectionInput(selection_, field);
}

function setInput($field, html) {
    $field.find('.js-field-text').replaceWith(html);
}