$(function () {
    "use strict";

    const $squads = $('#squads');

    const newSlot =
        '<div class="form-row align-items-center js-slot">' +
        '   <div class="col-md-1">' +
        '      <input class="form-control js-slot-number" type="number" required>' +
        '   </div>' +
        '   <div class="col-md-10">' +
        '      <input class="form-control js-slot-name" type="text" placeholder="Slot Name" required>' +
        '   </div>' +
        '   <div class="col-md-1 d-flex btn btn-trash" type="button"><i class="fa fa-trash-o"></i></div>' +
        '</div>';

    const newSquad =
        '<div class="form-group">' +
        '   <div class="form-row align-items-center js-squad">' +
        '       <div class="col-md-11">' +
        '           <input class="form-control js-squad-name" type="text" placeholder="Squad Name" required>' +
        '       </div>' +
        '       <div class="col-md-1 d-flex btn btn-trash" type="button"><i class="fa fa-trash-o"></i></div>' +
        '   </div>' +
        '' +
        '   <div class="ml-5 js-slots">' +
        newSlot +
        '       <div type="button" class="btn btn-success rounded-circle js-add-slot" title="Slot hinzufügen">' +
        '           <i class="fa fa-plus"></i>' +
        '       </div>' +
        '   </div>' +
        '</div>';

    $('#addSquad').on('click', function () {
        $squads.append(newSquad);
        $(this).appendTo($squads);
    });

    $squads.on('click', '.js-add-slot', function () {
        const $this = $(this);
        const $slots = $this.parent('.js-slots');
        $slots.append(newSlot);
        $this.appendTo($slots);
    });

    $squads.on('click', '.btn-trash', function () {
        const $row = $(this).parent('.form-row');
        if ($row.hasClass('js-squad')) {
            $row.parent('.form-group').remove();
        } else {
            $row.remove();
        }
    });
});