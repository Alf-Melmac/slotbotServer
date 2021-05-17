$(function () {
    "use strict";

    const $root = $('html');
    const $eyef = $('#eyef');
    let cx = $eyef.attr('cx');
    let cy = $eyef.attr('cy');

    $root.on('mousemove', function (evt) {
        const x = evt.clientX / innerWidth;
        const y = evt.clientY / innerHeight;

        $root.prop('--mouse-x', x);
        $root.prop('--mouse-y', y);

        cx = 115 + 30 * x;
        cy = 50 + 30 * y;
        $eyef.attr('cx', cx);
        $eyef.attr('cy', cy);
    });

    $root.on('touchmove', function (e) {
        const x = e.originalEvent.touches[0].clientX / innerWidth;
        const y = e.originalEvent.touches[0].clientY / innerHeight;

        $root.prop('--mouse-x', x);
        $root.prop('--mouse-y', y);
    });
});
