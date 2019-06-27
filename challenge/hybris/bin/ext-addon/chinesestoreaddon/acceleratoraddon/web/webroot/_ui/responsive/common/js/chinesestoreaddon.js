ACC.baiduMap = {
    init: function() {
        ACC.storefinder.initGoogleMap = function() {
            var storeInformation = ACC.storefinder.storeId;
            if ($(".js-store-finder-map").length > 0) {
                $(".js-store-finder-map").attr("id", "store-finder-map");
                ACC.baiduMap.draw("store-finder-map", storeInformation.latitude, storeInformation.longitude, storeInformation.name);
            }
        };
        ACC.pickupinstore.drawMap = function() {
            var storeInformation = ACC.pickupinstore.storeId;
            if ($("#colorbox .js-map-canvas").length > 0) {
                $("#colorbox .js-map-canvas").attr("id", "pickup-map");
                ACC.baiduMap.draw("pickup-map", storeInformation.storeLatitude, storeInformation.storeLongitude, storeInformation.name);
            }
        };
    },
    draw: function(id, latitude, longitude, name) {
        var map = new BMap.Map(id);
        var openInfo = function(content, e) {
            content =  ACC.common.encodeHtml(content);
            var p = e.target;
            var point = new BMap.Point(p.getPosition().lng, p.getPosition().lat);
            var infoWindow = new BMap.InfoWindow(content);
            map.openInfoWindow(infoWindow, point);
        };
        var point = new BMap.Point(longitude, latitude);
        var marker = new BMap.Marker(point);
        map.addOverlay(marker);
        marker.addEventListener("click",
        function(e) {
            openInfo(name, e);
        });
        map.centerAndZoom(point, 16);
        map.addControl(new BMap.MapTypeControl());
        map.enableScrollWheelZoom(true);
        var top_left_control = new BMap.ScaleControl({
            anchor: BMAP_ANCHOR_TOP_LEFT
        });
        var top_left_navigation = new BMap.NavigationControl();
        map.addControl(top_left_control);
        map.addControl(top_left_navigation);
    }
};
$(function() {
    // remove call google map for long time return in china.
    ACC.global.addGoogleMapsApi = function(callback) {
        if (callback !== undefined) {
            ACC.pickupinstore.drawMap();
        }
    };
    // modify size of input tag on pick up in store page.
    if ($("div#popup_store_pickup_form").length > 0) {
        $("div#popup_store_pickup_form").css({
            "right": -2500,
            "position": "absolute"
        }).show();
        var locationSearchButton = $("div#pickupModal button[data-id=pickupstore_location_search_button]")[0];
        var locationForSearch = $("div#pickupModal input[name=locationQuery]")[0];
        $(locationForSearch).outerHeight($(locationSearchButton).outerHeight());
        $("div#popup_store_pickup_form").hide();
    }
    // modify size of input tag on store navigation page.
    if ($("input#storelocator-query").length > 0) {
        $("input#storelocator-query").outerHeight($("form#storeFinderForm button").outerHeight());
    }
    //ACC.config.googleApiKey is already replaced by Baidu key in handler
    var script = document.createElement("script");
    script.src = "https://api.map.baidu.com/api?v=2.0&ak=" + encodeURIComponent(ACC.config.googleApiKey) + "&callback=ACC.baiduMap.init";
    document.body.appendChild(script);
});