$(function () {
    "use strict";
    $('input[type="file"]').on('change', function () {
        $('progress').show()
        const formData = new FormData();
        formData.append('file', this.files[0]);
        $.ajax(uploadSqmFileUrl, {
            method: 'POST',
            data: formData,
            cache: false,
            contentType: false,
            processData: false,

            // Custom XMLHttpRequest
            xhr: function () {
                var myXhr = $.ajaxSettings.xhr();
                if (myXhr.upload) {
                    // For handling the progress of the upload
                    myXhr.upload.addEventListener('progress', function (e) {
                        if (e.lengthComputable) {
                            $('progress').attr({
                                value: e.loaded,
                                max: e.total,
                            });
                        }
                    }, false);
                }
                return myXhr;
            }
        })
            .done(slotList => {
                addSlotList(slotList);
            })
            .fail(console.error);
    });
});
