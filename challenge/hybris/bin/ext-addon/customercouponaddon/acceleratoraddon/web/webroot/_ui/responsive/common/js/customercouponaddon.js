ACC.customerCoupon = {
    _autoload : [ 'onCouponReadMoreClick', 'autoCompleteCouponList', 'swicthNotification' ],
    handleApplyVoucher : function(e) {
        var voucherCode = $.trim($("#js-voucher-code-text").val());
        if (voucherCode !== '' && voucherCode.length > 0) {
            $("#applyVoucherForm").submit();
        }
    },

    onCouponReadMoreClick : function() {
        $(".js-coupon-read-more").click(function() {
            var title = ACC.common.encodeHtml($(this).data("colorbox-title"));
            var index = $(this).data("index");
            ACC.common.checkAuthenticationStatusBeforeAction(function() {
                ACC.colorbox.open(title, {

                    html : $(document).find("#coupon-popup-content-" + index).html(),
                    maxWidth : "100%",
                    width : "450px",
                    initialWidth : "450px"
                });
            });
        });
    },
    autoCompleteCouponList : function() {
        function split(val) {
            return val.split(/,\s*/);
        }
        function extractLast(term) {
            return split(term).pop();
        }
        var cache = {};
        $("#js-voucher-code-text").autocomplete({
            minLength : 0,
            width : 50,
            delay : 0,
            source : function(request, response) {
                var term = request.term;
                if (cache && term in cache) {
                    return response(cache[term]);
                }
                $.ajax({
                    url : ACC.config.encodedContextPath + '/cart/effectivecoupons',
                    type : 'GET',
                    async : false,
                    success : function(data) {
                        var autoSearchData = [];
                        var autoSearchNoData = [];
                        $.each(data, function(i, obj) {
                            autoSearchData.push({
                                value : obj.couponId,
                                label : obj.name + " (" + obj.couponCode + ")",
                                type : "autoSuggestion"
                            });
                        });
                        autoSearchNoData.push({
                            value : "",
                            label : ACC.common.encodeHtml(ACC.addons.customercouponaddon['customer.coupon.nocustomercoupon'])
                        });
                        if (autoSearchData.length == 0) {
                            cache[term] = autoSearchNoData;
                            return response(autoSearchNoData);
                        }
                        cache[term] = $.ui.autocomplete.filter(autoSearchData, extractLast(request.term));
                        return response(cache[term]);
                    }
                });
            },
            select : function(event, ui, e) {
                $("#js-voucher-code-text").val(ui.item.value);
                ACC.customerCoupon.handleApplyVoucher(e);
            }
        }).focus(function() {
            $(this).autocomplete("search");
        }).autocomplete("widget").addClass("coupon-autocomplete-scroll");
    },
    swicthNotification : function() {
        $('input[name="my-checkbox"]').click(function() {
            $(".comm-switch-checkbox").attr("disabled", true);
            var notificationOn = $(this).attr("notification");
            var couponCode = $(this).data("coupon-code");
            var rmUrl = ACC.config.encodedContextPath + "/my-account/coupon/notification/" + couponCode;
            var aObj = $(this);
            ACC.common.checkAuthenticationStatusBeforeAction(function() {
                $.ajax({
                    url : rmUrl,
                    data : {
                        isNotificationOn : notificationOn
                    },
                    success : function(data) {
                        if (data === "success") {
                            if (notificationOn === "true") {
                                $(aObj).attr("notification", "false");
                            } else {
                                $(aObj).attr("notification", "true");
                            }
                            $(".comm-switch-checkbox").attr("disabled", false);
                        }
                    }
                });
            });
        });
    }
};

ACC.customer360 = {
    loadCoupons : function(params) {
        $('#customer-coupons').hide();
        $('#long-load-example').show();
        $.ajax({
            type : "GET",
            data : params,
            url : ACC.config.encodedContextPath + "/asm/customer-coupons",
            success : function(data) {
                $('#customer-coupons').html(data);
                $('#long-load-example').hide();
                $('#customer-coupons').show();
                ACC.customer360.resetPageScroll();
            },
            error : function() {
                $('#long-load-example').hide();
                $('#customer-coupons').html('Get customer coupons error!');               
            }
        });
    },
    switchCouponTab : function() {
        $('.menu_customer_coupons li').click(function(e) {
            if (!$(this).hasClass('nav-tabs-mobile-caret')) {
                $(this).siblings().removeClass('active');
                $(this).addClass('active');
                var type = $(this).attr('value');
                ACC.customer360.loadCoupons({
                    type : type,
                    text : ''
                });
            }
            $('#search-input')[0].value = '';
        });
    },
    handleCouponAction : function() {
        $('.js-coupons-action').click(function() {
            var action = $(this).data('action');
            var code = $(this).attr('id');
            var url = $(this).data('url');
            $.ajax({
                type : "POST",
                data : {
                    action : action
                },
                url : url,
                success : function(data) {
                    if (data) {
                        ACC.customer360.loadCoupons({
                            type : $('.menu_customer_coupons .active').attr('value'),
                            text : $('#search-input')[0].value
                        });
                    }
                },
                error : function(data) {
                    $('#customer-coupons').html(action + ' coupon[' + ACC.common.encodeHtml(code) + '] error!');
                }
            });
        });
    },
    searchHandler : function() {
        var type = $('.menu_customer_coupons .active').attr('value');
        var text = $('#search-input')[0].value;
        ACC.customer360.loadCoupons({
            type : type,
            text : text
        });
    },
    handleSearch : function() {
        $('#coupons-search-btn').click(function() {
            ACC.customer360.searchHandler();
        });
        $('#search-input').keydown(function(e) {
            if (e.keyCode === 13) {
                ACC.customer360.searchHandler();
            }
        });
    },
    resetPageScroll : function() {
        var offset = $('body').scrollTop();
        $('body').scrollTop(0);
        $.colorbox.resize();
        $('body').scrollTop(offset);
    }
};

$(window).resize(function() {
    $("#js-voucher-code-text").autocomplete("close");
    $("#js-voucher-code-text").autocomplete("search");
});

