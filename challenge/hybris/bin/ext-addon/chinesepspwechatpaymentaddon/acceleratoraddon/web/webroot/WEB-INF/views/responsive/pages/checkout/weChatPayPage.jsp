<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="cleanedOrderCode" value="${fn:escapeXml(orderCode)}"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
	<jsp:attribute name="pageScripts">
		<script type="text/javascript" src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
		<script type="text/javascript">
			var timer;
			function onBridgeReady() {
				WeixinJSBridge.invoke('getBrandWCPayRequest', {
					"appId" : '${paymentData.appId}',
					"timeStamp" : '${paymentData.timeStamp}',
					"nonceStr" : '${paymentData.nonceStr}',
					"package" : '${paymentData.packageName}',
					"signType" : '${paymentData.signType}',
					"paySign" : '${paymentData.paySign}',
				}, function(res) {
					clearTimeout(timer);
					$(".btn-wechat").each(function(){
						$(this).removeAttr("disabled");
					});
					var status = res.err_msg.split(':')[1];
					if (status == 'ok'){
						window.location.href = ACC.config.encodedContextPath + '/checkout/multi/summary/checkPaymentResult/${orderCode}';
					}
					else if (status == 'fail'){
						showTimeoutPage();
						alert("<spring:theme code='wechatpay.failed.text'/>");
					}
				});
				$.ajax({
					type: "POST",
					url: ACC.config.encodedContextPath + "/checkout/multi/wechat/startPay",
					async: true,
					data: {"orderCode" : "${orderCode}"}
				});

			}
			if (typeof WeixinJSBridge == "undefined") {
				if (document.addEventListener) {
					document.addEventListener('WeixinJSBridgeReady', onBridgeReady,
							false);
				} else if (document.attachEvent) {
					document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
					document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
				}
			} else {
				onBridgeReady();
			}
		
			$(function(){
				$("#continue-pay-btn").click(function(){
					showPayingPage();
					onBridgeReady();
					startTimer();
				});
				startTimer();
			});
			
			function startTimer() {
				var t=parseInt("${wechatPayTimeout}")*1000;
				timer = setTimeout("showTimeoutPage()",t);
			}
			
			function showTimeoutPage() {
				clearTimeout(timer);
				$("#payingHeadline").hide();
				$("#timeoutHeadline").show();
				$(".btn-wechat").each(function(){
					$(this).removeAttr("disabled");
				});
			}
			
			function showPayingPage() {
				clearTimeout(timer);
				$("#payingHeadline").show();
				$("#timeoutHeadline").hide();
				$(".btn-wechat").each(function(){
					$(this).attr("disabled","disabled");
				});
			}
		</script>
	</jsp:attribute>
	
	<jsp:body>
		<div id="payingHeadline" class="checkout-headline">
			<spring:theme code="wechatpay.paying.headline.text"/>
		</div>
		<div id="timeoutHeadline" class="checkout-headline" style=display:none>
			<spring:theme code="wechatpay.timeout.headline.text"/>
		</div>
		
		<div class="row">
			<div class="col-sm-5 col-md-4 col-lg-4">
				<div class="checkout-indent">
					<div class="col-lg-7 col-md-12 col-sm-12">
						<a id="continue-pay-btn" class="btn btn-primary btn-block btn-wechat" href="javascript:;" disabled="disabled">
							<spring:theme code="wechat.pay.continue.button.text"/>
						</a><br>

						<a id="return-home-btn" class="btn btn-default btn-block btn-wechat" href="<spring:url value='/my-account/order/${cleanedOrderCode}' htmlEscape='false'/>" disabled="disabled">
							<spring:theme code="wechat.pay.homepage.button.text"/>
						</a>
					</div>
				</div>
			</div>
		</div>
	</jsp:body>
</template:page>