/**
 * add isEmpty function to String type
 */
String.prototype.isEmpty = function() {
    return (this.length === 0 || !this.trim());
};
/**
 * @Override the old function in base accelerator
 */
ACC.address.bindCountrySpecificUnitAddressForms = function() {
    $('#unitAddressCountrySelector select').filter('#address\\.country').on("change",
    function() {
        var options = {
            'addressCode': '',
            'unit':'',
            'countryIsoCode': $(this).val()
        };
        ACC.address.displayCountrySpecificUnitAddressForm(options);
    });
};
ACC.address.displayCountrySpecificUnitAddressForm = function(options) {
    var form = $("#addressForm");
    if (form.length > 0){
       var url = decodeURI(form.attr("action"));
       var i = url.indexOf("?");
       var params = url.substring(i+1,url.length);
       options.addressCode = ACC.chinesecommerceorg.getQueryString("addressId",params);
       options.unit = ACC.chinesecommerceorg.getQueryString("unit",params);
    }
    $.ajax({
        url: ACC.config.encodedContextPath + "/my-company/organization-management/manage-units/addressform",
        async: true,
        crossDomain: false,
        data: options,
        dataType: "html"
    }).done(function(data) {
        $("#unitAddressCountrySelector .i18nAddressForm").html($(data).html());
    }).always(function() {
        ACC.address.initChineseUnitAddressForm();
    });
};
/**
 * reload city items after region changed
 */
ACC.address.onUnitRegionChanged = function() {
    $("select#unitaddress\\.region").change(function() {
        var url = ACC.config.encodedContextPath + '/my-company/organization-management/manage-units/region/' + $(this).val();
        $.getJSON(url,
        function(data) {
            var $cities = $('select#unitaddress\\.townCity'),
            defaultOption = $('select#unitaddress\\.townCity > option:first');
            $cities.empty().append($(defaultOption).removeAttr("disabled").prop("selected", "selected"));
            $.each(data,
            function(item) {
                $cities.append($("<option />").val(this.code).text(this.name));
            });
            $('#unitaddress\\.district > option:first').removeAttr("disabled").attr('selected', 'selected');
            $('#unitaddress\\.district > option:gt(0)').remove();
        });
    });
};
/**
 * reload district items after city changed
 */
ACC.address.onUnitCityChanged = function() {
    $("select#unitaddress\\.townCity").change(function() {
        var cityCode = $(this).val();
        if (!cityCode.isEmpty()) {
            var url = ACC.config.encodedContextPath + '/my-company/organization-management/manage-units/city/' + cityCode;
            $.getJSON(url,
            function(data) {
                var $districts = $('select#unitaddress\\.district'),
                defaultOption = $('select#unitaddress\\.district > option')[0];
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
ACC.address.onUnitDistrictChanged = function() {
    $("select#unitaddress\\.district").change(function() {
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
/**
 * init Chinese address form
 */
ACC.address.initChineseUnitAddressForm = function() {
    ACC.address.bindCountrySpecificUnitAddressForms();
    ACC.address.onUnitRegionChanged();
    ACC.address.onUnitCityChanged();
    ACC.address.onUnitDistrictChanged();
    ACC.address.setMaxLengthForCellPhone();
};
ACC.address.addIdForCountrySelector = function() {
    $(".account-section-content > form > .row").attr("id", "unitAddressCountrySelector");
    $("#unitAddressCountrySelector > div").not(':first').not(':last').wrapAll("<div id='i18nAddressForm' class='i18nAddressForm' />");
};
/**
 * register change event on region/city when manage-units add-address and edit-address url are matched
 */
$(function() {
    var manageUnitAddAddress = '/my-company/organization-management/manage-units/add-address';
    var manageUnitEditAddress = '/my-company/organization-management/manage-units/edit-address';
    var requestUrl = window.location.href;
    if (requestUrl.indexOf(manageUnitAddAddress) > -1 || requestUrl.indexOf(manageUnitEditAddress) > -1) {
        ACC.address.addIdForCountrySelector();
        ACC.address.bindCountrySpecificUnitAddressForms();
        ACC.address.initChineseUnitAddressForm();
    }
});

ACC.chinesecommerceorg = {

        _autoload: [
            "editUnitAddress"
        ],
        editUnitAddress : function() {
                var edit = $(".account-list > .account-list-header > .account-list-header-add > .edit");
                if (edit.length > 0){
                var editUrl = decodeURI(edit.attr("href"));
                var i= editUrl.indexOf("?");
                var params = editUrl.substring(i+1,editUrl.length);
                var unit = ACC.chinesecommerceorg.getQueryString("unit",params);
                $.ajax({
                    url: ACC.config.encodedContextPath + "/my-company/organization-management/manage-units/formataddress",
                    type : "GET",
                    async : false,
                    data: { "unit" : unit },
                    dataType: "html",
                    success: function (data){
                        $(".account-list > .account-cards > .row").first().html(data);
                    }
                });
                }
        },

        getQueryString : function(name,params) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
            var r =params.match(reg);
            return r ? unescape(r[2]) : null;
        }
};

