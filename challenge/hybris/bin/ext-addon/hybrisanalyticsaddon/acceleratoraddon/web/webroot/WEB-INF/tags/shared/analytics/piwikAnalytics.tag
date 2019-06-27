<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<%--<c:if test="${not empty piwikSiteId}">--%>
<!-- Piwik -->
<script type="text/javascript">
	var analyticsConfig;
	if(typeof ACC != 'undefined'){
		analyticsConfig = ACC.addons.hybrisanalyticsaddon;
	}else{
		analyticsConfig = ACCMOB.addons.hybrisanalyticsaddon;
	}
	var piwikTrackerUrl = "${ycommerce:encodeJavaScript(PIWIK_TRACKER_ENDPOINT_HTTPS_URL)}"; // Use Piwik HTTPS Endpoint by default
	var piwikSiteId = "${ycommerce:encodeJavaScript(piwikSiteId)}";
	var sessionId = "${ycommerce:encodeJavaScript(SESSION_ID)}";

	if(location.protocol != 'https:')	// if the current page does not use https protocol, use the normal HTTP endpoint:
	{
		piwikTrackerUrl = "${ycommerce:encodeJavaScript(PIWIK_TRACKER_ENDPOINT_URL)}";
	}

	var tracker = Piwik.getAsyncTracker(piwikTrackerUrl, piwikSiteId);

	tracker.setTrackerUrl(piwikTrackerUrl);
	tracker.setSiteId(piwikSiteId);
	tracker.setRequestMethod('POST');
	tracker.setRequestContentType('application/json; charset=UTF-8');

	var processPiwikRequest = (function (request) {
		try {
			var pairs = request.split('&');

			var requestParametersArray = {};
			for (index = 0; index < pairs.length; ++index) {
				var pair = pairs[index].split('=');
				requestParametersArray[pair[0]] = decodeURIComponent(pair[1] || '');
			}
			requestParametersArray['sessionId'] = sessionId;

			return JSON.stringify(requestParametersArray);
		} catch (err) {
			return request;
		}
	});

	tracker.setCustomRequestProcessing(processPiwikRequest);

	var hybrisAnalyticsPiwikPlugin = (function() {
		function _getEventtypeParam(suffix) { return '&eventtype=' + suffix; }
		function ecommerce() { return _getEventtypeParam("ecommerce"); }
		function event() { return _getEventtypeParam("event"); }
		function goal() { return _getEventtypeParam("goal"); }
		function link() { return _getEventtypeParam("link"); }
		function load() { return _getEventtypeParam("load"); }
		function log() { return _getEventtypeParam("log"); }
		function ping() { return _getEventtypeParam("ping"); }
		function run() { return _getEventtypeParam("run"); }
		function sitesearch() { return _getEventtypeParam("sitesearch"); }
		function unload() { return _getEventtypeParam("unload"); }

		return {
			ecommerce: ecommerce,
			event : event,
			goal : goal,
			link : link,
			load : load,
			log : log,
			ping: ping,
			sitesearch : sitesearch,
			unload : unload
		};
	})();
	tracker.addPlugin('hybrisAnalyticsPiwikPlugin', hybrisAnalyticsPiwikPlugin);
	// Hybris Analytics specifics - END


	<c:choose>
		<c:when test="${pageType == 'PRODUCT'}">
			//View Product event
			<c:set var="categoriesJavaScript" value="" />
				<c:forEach items="${product.categories}" var="category">
				  <c:set var="categoriesJavaScript">${categoriesJavaScript},'${ycommerce:encodeJavaScript(category.code)}'</c:set>
				</c:forEach>
				tracker.setEcommerceView('${ycommerce:encodeJavaScript(product.code)}',  // (required) SKU: Product unique identifier
								'${ycommerce:encodeJavaScript(product.name)}',  // (optional) Product name
								[${fn:substringAfter(categoriesJavaScript, ',')}],  // (optional) Product category, or array of up to 5 categories
								'${ycommerce:encodeJavaScript(product.price.value)}');

				tracker.trackPageView('ViewProduct');  //Do we really need this ??
		</c:when>



		<c:when test="${pageType == 'CATEGORY'}">
		//View category - Start
			<c:choose>
				<c:when test="${searchPageData.pagination.totalNumberOfResults > 0}">
					<c:if test="${not empty breadcrumbs}">
						tracker.trackSiteSearch('${ycommerce:encodeJavaScript(searchPageData.freeTextSearch)}',  // Search keyword searched for
								'${ycommerce:encodeJavaScript(categoryCode)}:${ycommerce:encodeJavaScript(categoryName)}',  // Search category selected in your search engine. If you do not need this, set to false
								'${ycommerce:encodeJavaScript(searchPageData.pagination.totalNumberOfResults)}',// Number of results on the Search results page. Zero indicates a 'No Result Search Keyword'. Set to false if you don't know
								tracker.setCustomData('categoryName', '${ycommerce:encodeJavaScript(categoryName)}'));
					</c:if>
				</c:when>
				<c:otherwise>
						tracker.trackSiteSearch('${ycommerce:encodeJavaScript(searchPageData.freeTextSearch)}',  // Search keyword searched for
								false,  // Search category selected in your search engine. If you do not need this, set to false
								'0'  // Number of results on the Search results page. Zero indicates a 'No Result Search Keyword'. Set to false if you don't know
						);
				</c:otherwise>
			</c:choose>
		</c:when>



		<c:when test="${pageType == 'PRODUCTSEARCH'}">
		//View Product search - START
			<c:choose>
				<c:when test="${searchPageData.pagination.totalNumberOfResults > 0}">
					<c:if test="${not empty breadcrumbs}">
						<c:set var="categoriesJavaScript" value="" />
						<c:forEach items="${breadcrumbs}" var="breadcrumb">
							<c:set var="categoriesJavaScript">${categoriesJavaScript},'${ycommerce:encodeJavaScript(breadcrumb.name)}'</c:set>
						</c:forEach>
						tracker.trackSiteSearch('${ycommerce:encodeJavaScript(searchPageData.freeTextSearch)}',  // Search keyword searched for
								[${fn:substringAfter(categoriesJavaScript, ',')}],  // Search category selected in your search engine. If you do not need this, set to false
								'${ycommerce:encodeJavaScript(searchPageData.pagination.totalNumberOfResults)}'  // Number of results on the Search results page. Zero indicates a 'No Result Search Keyword'. Set to false if you don't know
						);
					</c:if>
				</c:when>
				<c:otherwise>
					tracker.trackSiteSearch('${ycommerce:encodeJavaScript(searchPageData.freeTextSearch)}',  // Search keyword searched for
							false,  // Search category selected in your search engine. If you do not need this, set to false
							'0'  // Number of results on the Search results page. Zero indicates a 'No Result Search Keyword'. Set to false if you don't know
					);
				</c:otherwise>
			</c:choose>
		</c:when>


		<c:when test="${pageType == 'ORDERCONFIRMATION'}">
			<c:forEach items="${orderData.entries}" var="entry">
				tracker.setCustomVariable(1,"ec_id","${ycommerce:encodeJavaScript(orderData.code)}","page");
				<c:forEach items="${entry.product.categories}" var="category">
					<c:set var="categoriesJavaScript">${categoriesJavaScript},'${ycommerce:encodeJavaScript(category.code)}'</c:set>
				</c:forEach>
				tracker.setEcommerceView('${ycommerce:encodeJavaScript(entry.product.code)}',  // (required) SKU: Product unique identifier
						'${ycommerce:encodeJavaScript(entry.product.name)}',  // (optional) Product name
						[${fn:substringAfter(categoriesJavaScript, ',')}],  // (optional) Product category. You can also specify an array of up to 5 categories eg. ["Books", "New releases", "Biography"]
						'${ycommerce:encodeJavaScript(entry.product.price.value)}'  // (recommended) Product price
				);
				tracker.addEcommerceItem('${ycommerce:encodeJavaScript(entry.product.code)}',  // (required) SKU: Product unique identifier
						'${ycommerce:encodeJavaScript(entry.product.name)}', // (optional) Product name
						[${fn:substringAfter(categoriesJavaScript, ',')}],  // (optional) Product category. You can also specify an array of up to 5 categories eg. ["Books", "New releases", "Biography"]
						'${ycommerce:encodeJavaScript(entry.product.price.value)}',  // (recommended) Product price
						'${ycommerce:encodeJavaScript(entry.quantity)}'.toString()  // (optional, default to 1) Product quantity
				);

			</c:forEach>
			tracker.trackEcommerceOrder('${ycommerce:encodeJavaScript(orderData.code)}',  // (required) Unique Order ID
					'${ycommerce:encodeJavaScript(orderData.totalPrice.value)}',  // (required) Order Revenue grand total (includes tax, shipping, and subtracted discount)
					'${ycommerce:encodeJavaScript(orderData.totalPrice.value - orderData.deliveryCost.value)}',  // (optional) Order sub total (excludes shipping)
					'${ycommerce:encodeJavaScript(orderData.totalTax.value)}',  // (optional) Tax amount
					'${ycommerce:encodeJavaScript(orderData.deliveryCost.value)}',  // (optional) Shipping amount
					false  // (optional) Discount offered (set to false for unspecified parameter)
			);

			tracker.setCustomVariable(1,"ec_id","${ycommerce:encodeJavaScript(orderData.code)}","page");

			tracker.trackEvent('checkout', 'success', '','');
			tracker.trackPageView(); // we recommend to leave the call to trackPageView() on the Order confirmation page
		</c:when>


		<c:otherwise>
			//Otherwise default to page view event
			tracker.trackPageView('ViewPage');
		</c:otherwise>
	</c:choose>

	tracker.enableLinkTracking();
	// handlers and their subscription for cart events


	function trackAddToCart_piwik(productCode, quantityAdded, cartData) {
		tracker.setEcommerceView(String(cartData.productCode),  // (required) SKU: Product unique identifier
				cartData.productName,  // (optional) Product name
				[ ], // (optional) Product category, string or array of up to 5 categories
				quantityAdded+""  // (optional, default to 1) Product quantity
		);
		tracker.addEcommerceItem(String(cartData.productCode),  // (required) SKU: Product unique identifier
				cartData.productName,  // (optional) Product name
				[ ], // (optional) Product category, string or array of up to 5 categories
				cartData.productPrice+"", // (recommended) Product price
				quantityAdded+""  // (optional, default to 1) Product quantity
		);

		if (!cartData.cartCode)
		{
			cartData.cartCode="${ycommerce:encodeJavaScript(cartData.code)}";
		}
		tracker.setCustomVariable(1,"ec_id",cartData.cartCode,"page");
		tracker.trackEcommerceCartUpdate(
				'0'  // (required) Cart amount
		);
	}

	function trackBannerClick_piwik(url) {
		tracker.setCustomVariable(1,"bannerid",url,"page");
		tracker.trackLink(url, 'banner');
	}

	function trackContinueCheckoutClick_piwik() {
		tracker.setCustomVariable(1,"ec_id","${ycommerce:encodeJavaScript(cartData.code)}","page");
		tracker.trackEvent('checkout', 'proceed', '','');
	}

    function trackShowReview_piwik() {
        tracker.trackEvent('review', 'view', '', '');
    }

	function trackUpdateCart_piwik(productCode,initialQuantity,newQuantity,cartData) {
		if (initialQuantity != newQuantity) {
			trackAddToCart_piwik(productCode, newQuantity,cartData);
		}
	}

	function trackRemoveFromCart_piwik(productCode, initialQuantity,cartData) {
		trackAddToCart_piwik(productCode, 0, cartData);
	}

	window.mediator.subscribe('trackAddToCart', function(data) {
		if (data.productCode && data.quantity) {
			trackAddToCart_piwik(data.productCode, data.quantity,data.cartData);
		}
	});

	window.mediator.subscribe('trackUpdateCart', function(data) {
		if (data.productCode && data.initialCartQuantity && data.newCartQuantity) {
			trackUpdateCart_piwik(data.productCode,data.initialCartQuantity,data.newCartQuantity,data);
		}
	});

	window.mediator.subscribe('trackRemoveFromCart', function(data) {
		if (data.productCode && data.initialCartQuantity) {
			trackRemoveFromCart_piwik(data.productCode, data.initialCartQuantity,data);
		}
	});

    window.mediator.subscribe('trackShowReviewClick', function() {
        trackShowReview_piwik();
    });

	$('.simple-banner').click(function(){
 		trackBannerClick_piwik( $(this).find("a").prop('href'));
	});

	$('.js-continue-checkout-button').click(function(){
		trackContinueCheckoutClick_piwik();
	});

</script>

<noscript><p><img src="${fn:escapeXml(piwikTrackerUrl)}?idsite=${fn:escapeXml(piwikSiteId)}" style="border:0;" alt="" /></p></noscript>

