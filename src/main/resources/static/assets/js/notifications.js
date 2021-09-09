$(function () {
    "use strict";

    //Only allow positive whole numbers
    $('#notificationForm').on('keypress', '.js-input-int', event => {
        const charCode = event.charCode;
        return (charCode === 8 || charCode === 0 || charCode === 13) ? null : charCode >= 48 && charCode <= 57;
    });

    const newNotification =
        `<div class="form-row d-flex align-items-center js-notification-settings-row">
            <div class="btn btn-xl btn-wht p-0 js-trash" type="button">
                    <em class="far fa-trash-alt"></em>
            </div>
            <div class="form-group col-2 m-0">
                <div class="input-group">
                    <input class="form-control js-input-int js-notification-hours" type="number" min="0">
                    <div class="input-group-append">
                        <span class="input-group-text">Stunden</span>
                    </div>
                </div>
            </div>
            <div class="form-group col-2 m-0">
                <div class="input-group">
                    <input class="form-control js-input-int js-notification-minutes" type="number" min="0">
                    <div class="input-group-append">
                        <span class="input-group-text">Minuten</span>
                    </div>
                </div>
            </div>
            <div class="col-7"><span>vor dem Event</span></div>
        </div>`;

    const $notificationDetailSettings = $('#notificationDetailSettings');
    $('#addNotification').on('click', function () {
        $notificationDetailSettings.append(newNotification);
        $(this).appendTo($notificationDetailSettings);
    });

    $notificationDetailSettings.on('click', '.js-trash', function () {
        $(this).parents('.form-row').remove();
    });

    const $notificationDetails = $('#notificationDetails');
    $('#notificationSwitch').on('change', function () {
        if ($(this).is(':checked')) {
            $notificationDetails.show();
        } else {
            $.ajax(deleteAllByUserUrl, {
                    method: 'DELETE'
                }
            )
                .done(showSavedToast)
                .fail(showErrorToast);
            $notificationDetails.hide();
        }
    });

    $('#btnSaveNotifications').on('click', function () {
        prepBeforeSave();

        $.ajax(putNotificationSettingsUrl, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(getNotificationSettings())
        })
            .done(showSavedToast)
            .fail(showErrorToast);
    });
});

function prepBeforeSave() {
    // noinspection JSJQueryEfficiency
    $('.js-notification-settings-row').each(function (index, element) {
        const $row = $(element);

        const $hours = $row.find('.js-notification-hours');
        const hours = $hours.val();
        const $minutes = $row.find('.js-notification-minutes');
        const minutes = $minutes.val();

        if (!hours && !minutes) {
            $row.find('.js-trash').trigger('click');
        } else if (!hours) {
            $hours.val(0);
        } else if (!minutes) {
            $minutes.val(0);
        }
    });

    // noinspection JSJQueryEfficiency Reselection is needed after potential change above
    if ($('.js-notification-settings-row').length === 0) {
        const $notificationSwitch = $('#notificationSwitch');
        $notificationSwitch.prop('checked', !$notificationSwitch.is(':checked')).trigger('change');
    }
}

function getNotificationSettings() {
    const notificationSettings = [];
    $('.js-notification-settings-row').each(function (index, element) {
        const $row = $(element);

        notificationSettings.push({
            id: $row.data('notificationid'),
            hoursBeforeEvent: $row.find('.js-notification-hours').val(),
            minutesBeforeEvent: $row.find('.js-notification-minutes').val()
        });
    });
    return notificationSettings;
}
