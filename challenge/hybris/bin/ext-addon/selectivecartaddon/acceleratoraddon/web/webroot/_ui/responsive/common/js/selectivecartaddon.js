ACC.selectiveCart = {
    _autoload : [ 'bindItemSelection', 'bindAllSelection', 'removeItemSelection' ],
    bindItemSelection : function() {
        // enable all checkbox on load completion
        $("input:checkbox").removeAttr('disabled');
        $('.js-cart-select-item').click(function() {
            $("input:checkbox").attr('disabled', 'disabled');
            if (!this.checked) {
                var productCodes = [];
                productCodes.push($(this).data('productcode'));
                ACC.selectiveCart.submit($(this).parent(), $(this).data('uncheckurl'), productCodes);
            }
        });
        $('.js-wishlist-select-item').click(function() {
            $("input:checkbox").attr('disabled', 'disabled');
            if (this.checked) {
                var productCodes = [];
                productCodes.push($(this).data('productcode'));
                ACC.selectiveCart.submit($(this).parent(), $(this).data('checkurl'), productCodes);
            }
        });
    },
    bindAllSelection : function() {
        $('.js-cart-select-all').click(function() {
            var productCodes = [];
            var checked = this.checked;
            $('.js-cart-select-all').each(function() {
                this.checked = checked;
            });
            if (checked) {
                $('.js-wishlist-select-item').each(function() {
                    this.checked = checked;
                    productCodes.push($(this).data('productcode'));
                });
                $("input:checkbox").attr('disabled', 'disabled');
                ACC.selectiveCart.submit($(this).parent().parent(), $(this).data('checkurl'), productCodes);
            } else {
                $('.js-cart-select-item').each(function() {
                    this.checked = checked;
                    productCodes.push($(this).data('productcode'));
                });
                $("input:checkbox").attr('disabled', 'disabled');
                ACC.selectiveCart.submit($(this).parent().parent(), $(this).data('uncheckurl'), productCodes);
            }
        });
    },
    removeItemSelection : function() {
        $('.js-wishlist2-entry-action-remove-button').click(function() {
            var $button = $(this);
            ACC.common.checkAuthenticationStatusBeforeAction(function() {
                $("input:checkbox").attr('disabled', 'disabled');
                var removeActionUrl =  $button.data('removeurl');
                $.ajax({
                    url :removeActionUrl,
                    type : 'POST',
                    data : {
                        productCode : $button.data('productcode')
                    },
                    success : function() {
                        location.reload();
                    }
                });
            });
        });
    },
    submit : function(form, url, productCodes) {
        ACC.common.checkAuthenticationStatusBeforeAction(function() {
            for (i in productCodes) {
                if (productCodes[i] !== null || productCodes[i] !== '') {
                    form.append($("<input>").attr({'type':'hidden', 'name':'productCodes'}).val(ACC.common.encodeHtml(productCodes[i].toString())));
                }
            }
            form.attr('action',url).submit();
        });
    }
};

ACC.minicart.bindMiniCart = function() {
    $(document).on(
            "click",
            ".js-mini-cart-link",
            function(e) {
                if (!hasWishListDataOnly()) {
                    e.preventDefault();
                    var url = $(this).data("miniCartUrl");
                    var cartName = ($(this).find(".js-mini-cart-count").html() !== 0) ? $(this).data("miniCartName") : $(this)
                            .data("miniCartEmptyName");

                    ACC.colorbox.open(ACC.common.encodeHtml(cartName), {
                        href : url,
                        maxWidth : "100%",
                        width : "380px",
                        initialWidth : "380px"
                    });
                }
                ;
                function hasWishListDataOnly() {
                    var checkWishListUrl = '/cart/entries';
                    var flag = false;
                    $.ajax({
                        url : ACC.config.encodedContextPath + checkWishListUrl,
                        type : "GET",
                        async : false,
                        success : function(data) {
                            flag = data;
                        }
                    });
                    return flag;
                }
            });
    $(document).on("click", ".js-mini-cart-close-button", function(e) {
        e.preventDefault();
        ACC.colorbox.close();
    });
};