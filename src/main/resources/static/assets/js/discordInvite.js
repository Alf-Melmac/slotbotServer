let discordInvite = function () {
    let i = {};
    return {
        init: function (e) {
            i.inviteCode = void 0 !== e.inviteCode && e.inviteCode;
            i.title = void 0 !== e.title ? e.title : "";
            i.targetElement = void 0 !== e.targetElement ? e.targetElement : "#discordInviteBox";
        },
        render: function () {
            $(function ($) {
                const inviteCode = i.inviteCode;
                if (inviteCode && inviteCode !== "") {
                    $(i.targetElement).append(
                        '<div id="discordInvite">' +
                        '   <h5 id="introText" class="noselect loadHidden">DU WURDEST EINGELADEN, EINEM SERVER BEIZUTRETEN</h5>' +
                        '   <div id="discordData">' +
                        '       <div id="serverImg" class="discordLink loadHidden"></div>' +
                        '       <div id="discordInfo">' +
                        '           <div id="serverNameBox" class="discordLink">' +
                        '               <span class="noselect" id="serverName">' + i.title + '</span>' +
                        '           </div>' +
                        '           <div id="status" class="loadHidden">' +
                        '               <div id="statusIndicators" class="noselect">' +
                        '                   <i id="onlineInd"></i><span id="numOnline">... Online</span>' +
                        '                   <i id="offlineInd"></i><span id="numTotal">... Mitglieder</span>' +
                        '               </div>' +
                        '           </div>' +
                        '       </div>' +
                        '       <button type="button" class="discordLink" id="callToAction">' +
                        '           <div id="buttonText" class="noselect">Beitreten</div>' +
                        '       </button>' +
                        '   </div>' +
                        '</div>'
                    );
                    $.ajax({
                        url: "https://discordapp.com/api/v6/invite/" + inviteCode + "?with_counts=true",
                        success: function (result) {
                            $("#serverName").html(result.guild.name);
                            $("#serverImg").css("background-image", "url(https://cdn.discordapp.com/icons/" + result.guild.id + "/" + result.guild.icon + ".jpg)");
                            $("#numTotal").html(result.approximate_member_count.toLocaleString("de") + " Mitglieder");
                            $("#numOnline").html(result.approximate_presence_count.toLocaleString("de") + " Online");

                            $(".discordLink").on('click', function () {
                                $("#callToAction").html(
                                    '<div id="joinedDiscord">' +
                                    '   Beigetreten<i id="joinedCheckmark" class="fa fa-check" aria-hidden="true"></i>' +
                                    '</div>'
                                ).attr("id", "callToAction-clicked");
                                window.open("https://discordapp.com/invite/" + i.inviteCode, "_blank");
                            });
                            $(".loadHidden").show();
                        },
                        error: function (i) {
                            let e;
                            if (void 0 !== i.responseJSON) {
                                $("#buttonText").html(i.responseJSON.message);
                                $("#discordInfo").remove();
                            } else {
                                $("#discordData").remove();
                                e = !0
                            }
                            const $introText = $("#introText");
                            e ? $introText.html("ERROR: Invalid Data URL.") : $introText.html("An error has occurred.");
                            $introText.css("margin", 0).show()
                        }
                    });
                } else $(i.targetElement).html("Error: No Invite Code Provided").attr("id", "discordInviteError").css("display", "inline-block")
            });
        }
    }
}();
