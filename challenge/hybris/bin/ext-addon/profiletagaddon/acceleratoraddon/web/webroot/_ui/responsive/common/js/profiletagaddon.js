(function () {
    if ( typeof window.customEvent === "function" ) { return false; }
    function customEvent ( event, params ) {
      params = params || { bubbles: false, cancelable: false, detail: undefined };
      var evt = document.createEvent( 'customEvent' );
      evt.initcustomEvent( event, params.bubbles, params.cancelable, params.detail );
      return evt;
     }
    customEvent.prototype = window.Event.prototype;
    window.customEvent = customEvent;
  })();

window.mediator.subscribe('trackAddToCart', function notifyProfileTagAddToCart(data) {
    if (data.productCode && data.quantity) {
        try {
            if (data.cartData && data.cartData.categories) {
                data.cartData.categories = JSON.parse(data.cartData.categories);
            }
            var profileTagElement = document.querySelector("body");
            var notifyProfileTag = new customEvent('notifyProfileTagAddToCart', {detail: data});
            profileTagElement.dispatchEvent(notifyProfileTag);
        } catch(err){
        }
    }
});

window.mediator.subscribe('trackUpdateCart', function notifyProfileTagUpdateCart(data) {
    if (data.productCode && data.initialCartQuantity && data.newCartQuantity) {
        try {
            var profileTagElement = document.querySelector("body");
            var notifyProfileTag = new customEvent('notifyProfileTagUpdateCart', {detail: data});
            profileTagElement.dispatchEvent(notifyProfileTag);
        } catch (err) {
        }
    }
});

window.mediator.subscribe('trackRemoveFromCart', function notifyProfileTagRemoveFromCart(data) {
    if (data.productCode && data.initialCartQuantity) {
        try {
            var profileTagElement = document.querySelector("body");
            var notifyProfileTag = new customEvent('notifyProfileTagRemoveFromCart', {detail: data});
            profileTagElement.dispatchEvent(notifyProfileTag);
        } catch(err) {
        }
    }
});