ACC.payment.showWeChatPaymentMethod = function() {
    if (ACC.payment.isWeChatBrowser()) {
        $("li#wechatpay").show();
    }
};
ACC.payment.isWeChatBrowser = function() {
    var ua = window.navigator.userAgent.toLowerCase();
    return ua.match(/MicroMessenger/i) === 'micromessenger';
};
ACC.payment.isWechatPayment = function() {
    return $('#payRightNowButton') && ($('#payRightNowButton').attr("data-payment") === 'wechatpay') && ACC.payment.isWeChatBrowser();
};
$(function() {
    ACC.payment.showWeChatPaymentMethod();
    if (ACC.payment.isWechatPayment()) {
        $('#payRightNowButton').removeAttr("target");
    }
    if (ACC.payment.onPayRightNowButtonClick) {
        var func = ACC.payment.onPayRightNowButtonClick;
        ACC.payment.onPayRightNowButtonClick = function() {
            if (!ACC.payment.isWechatPayment()) {
                func();
            }
        };
    }
});