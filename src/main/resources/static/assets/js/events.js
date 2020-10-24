$(function () {
    "use strict";

    const calendarEl = document.getElementById('calendar');
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'de',
        /*headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },*/
        eventSources: [
            {
                url: getEventsUrl,
                color: 'blue'
            }
        ]
    });
    calendar.render();
});