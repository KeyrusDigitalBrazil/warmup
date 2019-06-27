ACC.profile = {
    times: 60,
    mobileCheckUrl: ACC.config.encodedContextPath + "/verification-code/mobile/check",
    registerBinding: function() {
        $("#register-code-btn").click(function() {
            ACC.profile.mobileBindingHandler(this, true, {
                "mobileNumber": $("#mobileNumber").val()
            },
            function() {
                $("#mobileNumber").removeAttr("readonly");
            });
        });
    },
    profileBinding: function() {
        $("#profile-code-btn").click(function() {
            ACC.profile.mobileBindingHandler(this, false, {
                "mobileNumber": $("#mobileNumber").val()
            },
            null);
        });
    },
    mobileUnbinding: function() {
        $("#unbind-code-btn").click(function() {
            ACC.profile.mobileBindingHandler(this, false, {
                "flag": "profile"
            },
            null);
        });
    },
    mobileBindingHandler: function(btn, mobileCheck, data, callBack) {
        if (mobileCheck) {
            var mobileNumber = $("#mobileNumber").val();
            var errorMsg = ACC.profile.validateMobileNumber(mobileNumber);
            if (errorMsg !== '') {
                ACC.profile.renderErrorStyle(errorMsg);
                return;
            }
            ACC.profile.clearErrorStyle();
        }
        $(btn).attr("disabled", "disabled");
        $("#mobileNumber").attr("readonly", "readonly");
        ACC.profile.receiveVerificationCode($(btn).attr("data-url"), data);
        var btnTxt = $(btn).text();
        var btnTxtSent = $("#after-send-btn-text").val();
        var interval = setInterval(function() {
            $(btn).text(btnTxtSent.replace("{0}", ACC.profile.times--));
            if (ACC.profile.times === -1) {
                clearInterval(interval);
                ACC.profile.times = 60;
                $(btn).text(btnTxt);
                $(btn).removeAttr("disabled");
                if (callBack != null) {
                    callBack.call();
                }
            }
        },
        1000);
    },
    receiveVerificationCode: function(url, data) {
        $.get(url, data,
        function(result) {
            result = ACC.config.encodedContextPath + result;
            ACC.profile.createLink(result, "_black");
        });
    },
    validateMobileNumber: function(mobileNumber) {
        var msg = '';
        $.ajax({
            url: ACC.profile.mobileCheckUrl,
            data: {
                "mobileNumber": mobileNumber
            },
            type: "GET",
            async: false,
            success: function(data) {
                msg = data;
            }
        });
        return msg;
    },
    renderErrorStyle: function(errorMsg) {
        $("#mobileNumber").parent().addClass("has-error");
        var $error = $("<div>").addClass("help-block").append($("<span>").attr("id","mobileNumber.errors"));
        $error.children().first().text(errorMsg);
        $("#mobileNumber").nextAll().each(function() {
            $(this).remove();
        });
        $("#mobileNumber").parent().append($error);
    },
    clearErrorStyle: function() {
        $("#mobileNumber").parent().removeClass("has-error");
        $("#mobileNumber").nextAll().each(function() {
            $(this).remove();
        });
    },
    onBindButtonClick: function() {
        $("#bind-btn").click(function() {
            var mobileNumber = $("#mobileNumber").val();
            var errorMsg = ACC.profile.validateMobileNumber(mobileNumber);
            if (errorMsg !== '') {
                ACC.profile.renderErrorStyle(errorMsg);
            } else {
                $("#verificationCodeForm").submit();
            }
        });
    },
    onUnbindButtonClick: function() {
        $("#unbind-btn").click(function() {
            var url = $(this).attr("data-url");
            ACC.profile.createLink(url, '');
        });
    },
    createLink: function(url, target) {
        var link = $("<a>").attr("href",url);
        if(target !== ''){
            link.attr("target",target);
        }
        var e = document.createEvent('MouseEvents');
        e.initEvent('click', true, true);
        $(link)[0].dispatchEvent(e);
        $(link).remove();
    }
};
$(function() {
    ACC.profile.onBindButtonClick();
    ACC.profile.onUnbindButtonClick();
    ACC.profile.registerBinding();
    ACC.profile.profileBinding();
    ACC.profile.mobileUnbinding();
});