/**
 * @Override the old function in base accelerator
 */
ACC.address.displayCountrySpecificAddressForm = function(options, callback) {
    options.addressCode = $("input#addressId").val();
    $.ajax({
        url: ACC.config.encodedContextPath + "/my-account/addressform",
        async: true,
        data: options,
        dataType: "html",
        beforeSend: function() {
            $("div#i18nAddressForm").html(ACC.address.spinner);
        }
    }).done(function(data) {
        $("div#i18nAddressForm").html($(data).html());
        if (typeof callback == 'function') {
            callback.call();
        }
    }).always(function() {
        ACC.address.initChineseAddressForm();
    });
};
/**
 * reload city items after region changed
 */
ACC.address.onRegionChanged = function() {
    $("select#address\\.region").change(function() {
        var url = ACC.config.encodedContextPath + '/cn-address/region/' + $(this).val();
        $.getJSON(url,
        function(data) {
            var $cities = $('select#address\\.townCity'),
            defaultOption = $('select#address\\.townCity > option:first');
            $cities.empty().append($(defaultOption).removeAttr("disabled").prop("selected", "selected"));
            $.each(data,
            function(item) {
                $cities.append($("<option />").val(this.code).text(this.name));
            });
            $('#address\\.district > option:first').removeAttr("disabled").attr('selected', 'selected');
            $('#address\\.district > option:gt(0)').remove();
        });
    });
};
/**
 * reload district items after city changed
 */
ACC.address.onCityChanged = function() {
    $("select#address\\.townCity").change(function() {
        var cityCode = $(this).val();
        if (!cityCode.isEmpty()) {
            var url = ACC.config.encodedContextPath + '/cn-address/city/' + cityCode;
            $.getJSON(url,
            function(data) {
                var $districts = $('select#address\\.district'),
                defaultOption = $('select#address\\.district > option')[0];
                $districts.empty().append($(defaultOption).attr("selected", "selected").removeAttr("disabled"));
                $.each(data,
                function(item) {
                    $districts.append($("<option />").val(this.code).text(this.name));
                });
            });
            $(this).find("option:first").prop("disabled", true);
        }
    });
};
/**
 * disable the first option after district changed
 */
ACC.address.onDistrictChanged = function() {
    $("select#address\\.district").change(function() {
        if (!$(this).val().isEmpty()) {
            $(this).find("option:first").prop("disabled", true);
        }
    });
};
/**
 * cannot set the attribute in Hybris' tag, so use JavaScript
 */
ACC.address.setMaxLengthForCellPhone = function() {
    $("input#address\\.cellphone").attr("maxlength", "16");
};
ACC.address.displayChineseAddress = function() {
    
    $.ajax({
        url: ACC.config.encodedContextPath + "/checkout/multi/chineseAddressForm",
        async: true,
        dataType: "html"
    }).done(function(data) {
        var originAddress = $($("li.checkout-order-summary-list-heading").find("div.address")[0]).text();
        var fullNameWithTitle = $(data).find("#fullName").text().split(" ");
        fullNameWithTitle = fullNameWithTitle.filter(Boolean);
        var match = false;
        if (fullNameWithTitle.length > 0) {
            match = fullNameWithTitle.every(function(item) {
                return (originAddress.indexOf(item) >= 0);
            });
        }
        if (match) {
            $($("li.checkout-order-summary-list-heading").find("div.address")[0]).html(data);
        }
    });
};
ACC.address.displayChineseAddressInShippingItems = function() {
    $.ajax({
        url: ACC.config.encodedContextPath + "/checkout/multi/chineseAddressInShippingItems",
        async: true,
        dataType: "html"
    }).done(function(data) {
        var originAddress = $($("div.checkout-shipping-items .checkout-shipping-items-header")[1]).next().text();
        var fullNameWithTitle = $(data).find("#fullNameInShipping").text().split(" ");
        fullNameWithTitle = fullNameWithTitle.filter(Boolean);
        var match = false;
        if (fullNameWithTitle.length > 0) {
            match = fullNameWithTitle.every(function(item) {
                return (originAddress.indexOf(item) >= 0);
            });
        }
        if (match) {
            $($("div.checkout-shipping-items .checkout-shipping-items-header")[1]).next().html(data);
        }
    });
};
/**
 * Forbid use Chinese format delivery address as billing address here. Because the billing address
 * will be submitted to the credit card company and the new form Chinese format address make no
 * sense to the credit card company.
 *
 * If the countryIso is "CN" and lastname is empty, this is a Chinese format address
 */
ACC.address.forbidUseChineseDeliveryAddressAsBillingAddress = function() {
    var deliveryAddressData = $("#useDeliveryAddressData");
    var deliveryAddress = $("#useDeliveryAddress");
    if (deliveryAddressData.attr("data-countryisocode") === "CN" && !deliveryAddressData.attr("data-lastname")) {
        deliveryAddress[0].checked = false;
        deliveryAddress.parent().remove();
    }
};
/**
 * init Chinese address form
 */
ACC.address.initChineseAddressForm = function() {
    ACC.address.onRegionChanged();
    ACC.address.onCityChanged();
    ACC.address.onDistrictChanged();
    ACC.address.setMaxLengthForCellPhone();
};
/**
 * register change event on region/city once DOM (re)loaded
 */
$(function() {
    ACC.address.initChineseAddressForm();
    ACC.address.forbidUseChineseDeliveryAddressAsBillingAddress();
    ACC.address.displayChineseAddress();
    ACC.address.displayChineseAddressInShippingItems();
});