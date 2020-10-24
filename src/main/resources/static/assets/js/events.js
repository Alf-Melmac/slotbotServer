$(function () {
    const calendarEl = document.getElementById('calendar');
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'de',
        /*headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },*/
        events: [
            {
                title: 'All Day Event',
                start: '2020-10-01'
            },
            {
                title: 'Long Event',
                start: '2020-10-07',
                end: '2020-10-10'
            },
            {
                groupId: '999',
                title: 'Repeating Event',
                start: '2020-10-09T16:00:00'
            },
            {
                groupId: '999',
                title: 'Repeating Event',
                start: '2020-10-16T16:00:00'
            },
            {
                title: 'Conference',
                start: '2020-10-11',
                end: '2020-10-13'
            },
            {
                title: 'Meeting',
                start: '2020-10-12T10:30:00',
                end: '2020-10-12T12:30:00'
            },
            {
                title: 'Lunch',
                start: '2020-10-12T12:00:00'
            },
            {
                title: 'Meeting',
                start: '2020-10-12T14:30:00'
            },
            {
                title: 'Birthday Party',
                start: '2020-10-13T07:00:00'
            },
            {
                title: 'Click for Google',
                url: 'http://google.com/',
                start: '2020-10-28'
            }
        ]
    });
    calendar.render();
});