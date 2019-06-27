ACC.selectiveCartSplitList = {
    _autoload: ['saveItemSelection', 'removeItemSelection'],
    saveItemSelection: function() {
        $('.js-wishlist-select-item').click(function() {
            ACC.selectiveCartSplitList.submit($(this).parent(), $(this).data('checkurl'), $(this).data('productcode'));
        });
    },
    removeItemSelection: function() {
        $('.js-wishlist2-entry-action-remove-button').click(function() {
            ACC.selectiveCartSplitList.submit($(this).parent(), $(this).data('removeurl'), $(this).data('productcode'));
        });
    },
    submit: function(form, url, productCode) {
        form.append($("<input>").attr({'type':'hidden', 'name':'productCode'}).val(productCode));
        
        ACC.common.checkAuthenticationStatusBeforeAction(function() {
            form.attr('action', url).submit();
        });
    }
};
ACC.cartitem.bindCartItem = function() {
    $('.js-execute-entry-action-button').on("click",
    function() {
        var entryAction = $(this).data("entryAction");
        var entryActionUrl = $(this).data("entryActionUrl");
        var entryProductCode = $(this).data("entryProductCode");
        var entryInitialQuantity = $(this).data("entryInitialQuantity");
        var actionEntryNumbers = $(this).data("actionEntryNumbers");
        if (entryAction === 'REMOVE') {
            ACC.track.trackRemoveFromCart(entryProductCode, entryInitialQuantity);
        }
        var cartEntryActionForm = $("#cartEntryActionForm");
        var entryNumbers = actionEntryNumbers.toString().split(';');
        entryNumbers.forEach(function(entryNumber) {
            var entryNumbersInput = $("<input>").attr("type", "hidden").attr("name", "entryNumbers").val(entryNumber);
            cartEntryActionForm.append($(entryNumbersInput));
        });
        if (entryAction === 'SAVEFORLATER') {
            ACC.common.checkAuthenticationStatusBeforeAction(function() {
                cartEntryActionForm.attr('action', entryActionUrl).submit();
            });
        } else {
            cartEntryActionForm.attr('action', entryActionUrl).submit();
        }
    });
    $('.js-update-entry-quantity-input').on("blur",
    function(e) {
        ACC.cartitem.handleUpdateQuantity(this, e);
    }).on("keyup",
    function(e) {
        return ACC.cartitem.handleKeyEvent(this, e);
    }).on("keydown",
    function(e) {
        return ACC.cartitem.handleKeyEvent(this, e);
    });
};
ACC.minicart.bindMiniCart = function() {
    $(document).on("click", ".js-mini-cart-link",
    function(e) {
        if (!hasWishListDataOnly()) {
            e.preventDefault();
            var url = $(this).data("miniCartUrl");
            var cartName = ($(this).find(".js-mini-cart-count").html() !== 0) ? $(this).data("miniCartName") : $(this).data("miniCartEmptyName");
            ACC.colorbox.open(ACC.common.encodeHtml(cartName), {
                href: url,
                maxWidth: "100%",
                width: "380px",
                initialWidth: "380px"
            });
        };
        function hasWishListDataOnly() {
            var checkWishListUrl = '/cart/entries';
            var flag = false;
            $.ajax({
                url: ACC.config.encodedContextPath + checkWishListUrl,
                type: "GET",
                async: false,
                success: function(data) {
                    flag = data;
                }
            });
            return flag;
        }
    });
    $(document).on("click", ".js-mini-cart-close-button",
    function(e) {
        e.preventDefault();
        ACC.colorbox.close();
    });
};