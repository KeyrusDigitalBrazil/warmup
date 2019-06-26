$(function() {
    $("#invoiceTitle").change(function() {
        $("#invoiceNamePanel").show();
    });
    $("#invoiceRequiredYes").click(function() {
        $("#invoiceTitle, #invoiceCategory, #invoiceName").removeAttr("disabled");
        $("#invoiceRequired").val("true");
    });
    $("#invoiceRequiredNo").click(function() {
        $("#invoiceTitle, #invoiceCategory, #invoiceName").attr("disabled", "disabled");
        $("#invoiceRequired").val("false");
    });
});