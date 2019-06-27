$(function() {
    ACC.payment.onWindowScroll();
    ACC.payment.onWindowResize();
    ACC.payment.countdown(5);
});

$(window).on("load",function(){
    ACC.payment.setBtnUsable();
    ACC.payment.onPayRightNowButtonClick();
});

ACC.payment.onPayRightNowButtonClick = function() {
    $('#payRightNowButton').click(function() {
        $('.mask,.payPop').fadeIn(300);
        ACC.payment.setMaskAttr();
        ACC.payment.setPayPopTop();
    });
};

ACC.payment.onWindowScroll = function() {
    $(window).scroll(function() {
        ACC.payment.setPayPopTop();
    });
};

ACC.payment.onWindowResize = function() {
    $(window).resize(function() {
        ACC.payment.setMaskAttr();
        ACC.payment.setPayPopTop();
    });
};

ACC.payment.setMaskAttr = function() {
    $(".mask").offset({
        top : 0,
        left : 0
    });
    $(".mask").width($(document).width());
    $(".mask").height($(document).height());
};

ACC.payment.setPayPopTop = function() {
    $(".pay-pop-container").width($(".pay-pop-container").prev().width());
    var w = ACC.payment.getScrollWidth();
    $(".payPop").offset({
        left: (document.body.clientWidth - w)/2 - $(".payPop").width()/2,
        top : $(window).scrollTop() + $(window).height() * 0.2
    });
};

ACC.payment.countdown = function(timeout) {
    var showbox = $("#countdown");
    if (showbox.length > 0) {
        $(showbox).html(timeout);
        if (timeout === 0) {
            window.opener = null;
            window.close();
        } else {
            timeout--;
            setTimeout("ACC.payment.countdown(" + timeout + ")", 1000);
        }
    }
};

ACC.payment.getScrollWidth = function() {
    var noScroll = 0, scroll = 0;
    var obj = $("<div/>");
    $(obj).css({
        "position" : "absolute",
        "top" : "-1000px",
        "width" : "100px",
        "height" : "100px",
        "overflow" : "hidden"
    });
    noScroll = $(obj).appendTo($("body"))[0].clientWidth;
    $(obj).css("overflowY", "scroll");
    scroll = $(obj)[0].clientWidth;
    $(obj).remove();
    return noScroll-scroll;
};

ACC.payment.setBtnUsable = function(){
    var href = $("#payRightNowButton").attr("data-href");
    $("#payRightNowButton").attr("href", href);
    $("#payRightNowButton").removeAttr("data-href");
    $("#payRightNowButton").removeAttr("disabled");
};
