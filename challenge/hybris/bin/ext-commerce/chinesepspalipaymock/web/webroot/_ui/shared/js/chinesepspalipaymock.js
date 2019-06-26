var onGetDirectPay = function(response_type) {
    var requestdata = "";
    var url = $("#mock").attr("action");
    var i = 0;
    $("input:hidden").each(function() {
        var key = $(this).attr("name");
        var value = $(this).val();
        var entry = key + "=" + value;
        if (i === 0) {
            requestdata += entry;
        } else {
            requestdata += "&" + entry;
        }
        i++;
    });
    requestdata += "&trade_status=" + $("#tradeStatus").val();
    requestdata += "&error_code=" + $("#errorCode").val();
    requestdata += "&action=" + response_type;
    $.ajax({
        type : "GET",
        url : url,
        data : requestdata,
        success : function(result) {
            alert(result); // NOSONAR
        }
    });
};
var isEmptyValue = function(value) {
    return (value == null || value === "");
};
var onClickNotify = function() {
    $("#notifyBtn").click(function() {
        onGetDirectPay($("#notifyBtn").val());
    });
};
var onSubmit = function() {
    $("#mock").submit(function() {
        return confirm("Confirm return!");
    });
};
var onClickError = function() {
    $("#notifyErrorBtn").click(function() {
        onGetDirectPay($("#notifyErrorBtn").val());
    });
};
var onClickRefund = function() {
    $("#notifyRefundBtn").click(function() {
        var tradeStatus = $("#tradeStatus").val();
        var errorCode = $("#errorCode").val();
        if (isEmptyValue(tradeStatus)) {
            alert("Please select Transaction Status"); // NOSONAR
            return;
        }
        if (isEmptyValue(errorCode)) {
            alert("Please select Error Code"); // NOSONAR
            return;
        }
        if (tradeStatus === "SUCCESS" && errorCode !== "SUCCESS") {
            alert("Please select SUCCESS as Error Code"); // NOSONAR
        } else {
            if (tradeStatus === "FAILED" && errorCode === "SUCCESS") {
                alert("Please select other Error Code for FAILED status");
            } else {
                onGetDirectPay($("#notifyRefundBtn").val());
            }
        }

    });
};
var onSubmitRefund = function(baseSite, orderCode) {
    var url = $("#refundMock").attr("action");
    $.ajax({
        type : "POST",
        url : url,
        data : {
            "baseSite" : baseSite,
            "orderCode" : orderCode
        },
        success : function(result) {
            var resstr = result;
            if (resstr.substring(0, 9) === "redirect:") {
                window.location.href = resstr.substring(9);
            } else {
                alert(result); // NOSONAR
            }
        }
    });
};
var onClickNext = function() {
    $("#nextBtn").click(function() {
        if (isEmptyValue($("#orderCode").val())) {
            alert("Please input Order #"); // NOSONAR
        } else {
            onSubmitRefund($("#baseSite").val(), $("#orderCode").val());
        }
    });
};
$(function() {
    onClickNotify();
    onSubmit();
    onClickError();
    onClickRefund();
    onClickNext();
});