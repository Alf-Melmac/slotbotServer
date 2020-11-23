$(function () {
    "use strict";
    const $logs = $('#logs');

    $logs.DataTable({
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.21/i18n/German.json"
        },
        ajax: {
            url: '/logs/list',
            dataSrc: ""
        },
        columns: [
            {
                data: 'user.name'
            },
            {
                data: 'user.id',
                render: function (userId) {
                    return `<div class="copy-to-clipboard">` +
                        `<input class="text-primary" type="text" ` +
                        ` value="${userId}" readonly>` +
                        `</div>` +
                        `<div class='copied'></div>`;
                }
            },
            {
                data: 'objectName'
            },
            {
                data: 'action'
            },
            {
                data: 'timeLeft'
            }
        ]
    });

    $logs.on('click', '.copy-to-clipboard input', function () {
        const $this = $(this);
        $this.focus();
        $this.select();
        document.execCommand('copy');
        $this.parent().siblings('.copied').text('Copied to clipboard').show().fadeOut(1200);
    });
});