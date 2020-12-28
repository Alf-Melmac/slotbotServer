$(function () {
    "use strict";
    const $logs = $('#logs');

    $logs.DataTable({
        language: {
            url: "//cdn.datatables.net/plug-ins/1.10.21/i18n/German.json"
        },
        processing: true,
        serverSide: true,
        orderMulti: false,
        ajax: function (data, callback) {
            let dataResult = {};
            dataResult.page = data.start / data.length;
            dataResult.size = data.length;
            dataResult.filter = data.search.value;

            let property = data.columns[data.order[0].column].data;
            switch (property) { //Map ActionLogDto to ActionLog attributes
                case 'user.id':
                case 'user.name':
                    property = 'user';
                    break;
                case 'timeLeft':
                    property = 'timeGap';
                    break;
                default:
                    break;
            }
            dataResult.sort = {
                property: property,
                direction: data.order[0].dir
            };

            $.ajax('/logs', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                data: JSON.stringify(dataResult)
            })
                .done(json => {
                    let recordsTotal = 0;
                    if (typeof json.page !== 'undefined') {
                        recordsTotal = json.page.totalElements;
                    }

                    let content = {};
                    if (typeof json._embedded !== 'undefined' && typeof json._embedded.actionLogDtoList !== 'undefined') {
                        content = $.map(json._embedded.actionLogDtoList, function (obj) {
                            return $.extend(true, {}, obj);
                        });
                    }

                    callback({
                        recordsTotal: recordsTotal,
                        recordsFiltered: recordsTotal,
                        aaData: content
                    });
                })
                .fail(console.log);
        },
        order: [4, 'asc'],
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
                data: 'objectName',
                orderable: false
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