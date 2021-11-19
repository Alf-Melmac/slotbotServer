$(function () {
    "use strict";

    $('#eventEdit').on('click', () => window.location.href = eventEditUrl);

    $('#eventCopy').on('click', () => window.location.href = createEventUrl);
});
