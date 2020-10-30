$(function () {
    "use strict";

    const $squads = $('#squads');

    //Only allow positive whole numbers
    $squads.on('keypress', '.js-slot-number', event => (event.charCode === 8 || event.charCode === 0 || event.charCode === 13) ? null : event.charCode >= 48 && event.charCode <= 57);

    const newSlot =
        '<div class="form-row align-items-center js-slot">' +
        '   <div class="col-md-1">' +
        '      <input class="form-control js-slot-number" type="number" min="1" value="{defaultSlotValue}" required>' +
        '   </div>' +
        '   <div class="col-md-10">' +
        '      <input class="form-control js-slot-name" type="text" placeholder="Slot Name" required>' +
        '   </div>' +
        '   <div class="col-md-1 d-flex btn btn-trash" type="button"><em class="fa fa-trash-o"></em></div>' +
        '</div>';

    const newSquad =
        '<div class="form-group js-complete-squad">' +
        '   <div class="form-row align-items-center js-squad">' +
        '       <div class="col-md-11">' +
        '           <input class="form-control js-squad-name" type="text" placeholder="Squad Name" required>' +
        '       </div>' +
        '       <div class="col-md-1 d-flex btn btn-trash" type="button"><em class="fa fa-trash-o"></em></div>' +
        '   </div>' +
        '' +
        '   <div class="ml-5 js-slots">' +
        newSlot +
        '       <div type="button" class="btn btn-success rounded-circle js-add-slot" title="Slot hinzufügen">' +
        '           <em class="fa fa-plus"></em>' +
        '       </div>' +
        '   </div>' +
        '</div>';

    $('#addSquad').on('click', function () {
        $squads.append(newSquad.replace('{defaultSlotValue}', findFirstUnusedSlotNumber()));
        $(this).appendTo($squads);
    });

    $squads.on('click', '.js-add-slot', function () {
        const $this = $(this);
        const $slots = $this.parent('.js-slots');
        $slots.append(newSlot.replace('{defaultSlotValue}', findFirstUnusedSlotNumber()));
        $this.appendTo($slots);
    });

    $squads.on('click', '.btn-trash', function () {
        const $row = $(this).parent('.form-row');
        if ($row.hasClass('js-squad')) {
            $row.parent('.js-complete-squad').remove();
        } else {
            $row.remove();
        }
    });

    function findFirstUnusedSlotNumber() {
        const slotNumbers = $.map($('.js-slot-number'), el => parseInt($(el).val()))
            .sort((a, b) => a - b);
        let slotNumber = 1;

        for (let i = 0; i < slotNumbers.length; i++) {
            const currentNumber = slotNumbers[i];
            while (slotNumber < currentNumber) {
                if (slotNumber !== currentNumber) {
                    return slotNumber;
                }

                slotNumber++;
            }
            slotNumber++;
        }
        return slotNumber;
    }
});