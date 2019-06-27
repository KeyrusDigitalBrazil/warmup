<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:htmlEscape defaultHtmlEscape="true"/>
<script type="text/javascript" src="${commonResourcePath}/js/jquery-3.2.1.min.js"></script>
<button id="share" class="btn btn-default btn-block"><img alt="logo" src="${contextPath}/_ui/addons/chineseproductsharingaddon/responsive/common/images/shareIcon.png" /></button>
<div id="qrtitle" style="display:none"><spring:theme code="type.ChineseProductSharingComponent.title" /></div>

<script type="text/javascript">
$("#share").click(function(){
	var qrtitle = $("#qrtitle").text();
	ACC.colorbox.open(qrtitle, {
	        href: ACC.config.encodedContextPath + "/product-share/qr?url="+encodeURIComponent(window.location.href),
			width:"400px",
	    	height:"450px",
			initialWidth :"320px",
			onComplete: function () {
                ACC.common.refreshScreenReaderBuffer();
            },
            onClosed: function () {
                ACC.common.refreshScreenReaderBuffer();
            }
	   });
});
</script>

