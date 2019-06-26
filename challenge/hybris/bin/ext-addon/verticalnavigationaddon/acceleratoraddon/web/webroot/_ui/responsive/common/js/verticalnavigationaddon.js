ACC.verticalNavigation = {
    _autoload: ["cleanup"],
    init: function() {
        $("#banner_menu_wrap").children().hover(function() {
            var offsetTop = $("#banner_menu_wrap").offset().top - $(this).offset().top + 3;
            var offsetHeight = $("#verticalNavigation").height() - 5;
            var subMenu = $(this).children(".banner_menu_content");
            $(subMenu).css({
                "top": offsetTop + "px",
                "height": offsetHeight + "px"
            }).show();
            if (!$(subMenu).find("li").hasClass("thumbnail_only")) {
                ACC.verticalNavigation.adjustWidth($(subMenu));
            }
        },
        function() {
            $(this).children(".banner_menu_content").hide();
        });
    },
    restore: function() {
        if ($(window).width() < 1023) {
            $("div.nav-bottom").show();
        } else if ($("#verticalNavigation").length > 0) {
            $("div.nav-bottom").hide();
        };
    },
    cleanup: function() {
        $("div.banner_menu_content").each(function() {
            if ($(this).find("li").length < 1) {
                $(this).remove();
            }
        });
    },
    adjustWidth: function(target) {
        var array = [];
        $(target).find("a").each(function() {
            array.push($(this).position().left);
        });
        var count = $.unique(array).length;
        if (count > 1) {
            $(target).css("width", count * 100 + "%").find("li").css("width", 1 / count * 100 + "%");
        } else {
            $(target).css("width", "150%").find("li").css("width", "100%");
        }
    }
};
/**
 * public method to delay an event
 *
 */
var delay = (function() {
    var timer = 0;
    return function(callback, ms) {
        clearTimeout(timer);
        timer = setTimeout(callback, ms);
    };
})();
/**
 * have to window.onload to get correct window's width
 */
$(window).on("load",
function() {
    ACC.verticalNavigation.init();
    ACC.verticalNavigation.restore();
});
/**
 * have to delay to avoid repeatedly response on resize event
 */
$(window).resize(function() {
    delay(function() {
        ACC.verticalNavigation.restore();
    },
    100);
});