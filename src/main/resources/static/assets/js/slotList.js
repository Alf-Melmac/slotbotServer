$(function () {
    "use strict";

    // Sortable
    const $sortable = $('.js-sortable');
    $sortable.sortable();

    const $squads = $('#squads');
    const $addSquad = $('#addSquad');

    //Only allow positive whole numbers
    $squads.on('keypress', '.js-slot-number', event => {
        const charCode = event.charCode;
        return (charCode === 8 || charCode === 0 || charCode === 13) ? null : charCode >= 48 && charCode <= 57;
    });

    const newSlot =
        `<div class="form-row align-items-center js-slot">
           <div class="col-md-1">
              <input class="form-control js-slot-number" type="number" min="1" value="{defaultSlotValue}" required>
           </div>
           <div class="col-md-10">
              <input class="form-control js-slot-name" type="text" placeholder="Slot Name" required>
           </div>
           <div class="col-md-1 d-flex">
                <div class="btn btn-xl btn-lock p-0 pr-1 js-lock" type="button" title="Blockierung">
                    <em class="fas fa-lock-open"></em>
                </div>
                <div class="btn btn-xl btn-wht p-0 js-trash" type="button">
                    <em class="far fa-trash-alt"></em>
                </div>
           </div>
        </div>`;

    const newSquad =
        `<div class="form-group js-complete-squad">
           <div class="form-row align-items-center js-squad">
               <div><em class="fas fa-arrows-alt-v"></em></div>
               <div class="col-md-11">
                   <input class="form-control js-squad-name" type="text" placeholder="Squad Name" required>
               </div>
               <div class="d-flex btn btn-xl btn-wht js-trash" type="button"><em class="far fa-trash-alt"></em></div>
           </div>
        
           <div class="ml-5 js-slots">
               ${newSlot}
               <div type="button" class="btn btn-success rounded-circle js-add-slot" title="Slot hinzufügen">
                   <em class="fas fa-plus"></em>
               </div>
           </div>
        </div>`;

    $addSquad.on('click', function () {
        $squads.append(newSquad.replace('{defaultSlotValue}', findFirstUnusedSlotNumber()));
        $(this).appendTo($squads);
    });

    if ($squads.hasClass('js-wizard')) { //Create initial squad in wizard
        $addSquad.trigger('click');
    }

    $squads.on('click', '.js-add-slot', function () {
        const $this = $(this);
        const $slots = $this.parent('.js-slots');
        $slots.append(newSlot.replace('{defaultSlotValue}', findFirstUnusedSlotNumber()));
        $this.appendTo($slots);
    });

    $squads.on('click', '.js-lock:not(.js-blocked, .btn-denied)', function () {
        $(this).find('.fas').toggleClass('fa-lock-open fa-lock');
    });

    $squads.on('click', '.js-trash', function () {
        const $row = $(this).parents('.form-row');
        if ($row.hasClass('js-squad')) {
            $row.parent('.js-complete-squad').remove();
        } else {
            $row.remove();
        }
    });

    function findFirstUnusedSlotNumber() {
        const slotNumbers = getSlotNumbers().sort((a, b) => a - b);
        let slotNumber = 1;

        for (const currentNumber of slotNumbers) {
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

    $('#renumber').on('click', function () {
        $squads.find('.js-slot-number').each(function (index) {
            $(this).val(index + 1);
        });
    });
});

function getSlotNumbers() {
    return $.map($('.js-slot-number'), el => parseInt($(el).val()));
}

function checkUniqueSlotNumbers() {
    const slotNumbers = getSlotNumbers();
    return slotNumbers.length === new Set(slotNumbers).size;
}

function addSlotList(slotList) {
    $('#uploadSlotlistModal').modal('hide'); //Close upload modal
    $('#squads').find('.js-trash').trigger('click'); //Remove all existing squads and slots

    for (const squad of slotList) {
        $('#addSquad').trigger('click');
        fillSquad($('.js-complete-squad').last(), squad);
    }
}

function fillSquad($squad, squad) {
    $squad.find('.js-squad-name').val(squad.name);
    let first = true;
    for (const slot of squad.slotList) {
        if (!first) {
            $squad.find('.js-add-slot').trigger('click');
        } else {
            first = false;
        }
        const $slot = $squad.find('.js-slot').last();
        if (slot.number !== 0) {
            $slot.find('.js-slot-number').val(slot.number);
        }
        if (slot.blocked) {
            $slot.find('.js-lock').trigger('click');
        }
        $slot.find('.js-slot-name').val(slot.name);
    }
}
