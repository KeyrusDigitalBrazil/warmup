<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="row">
	<div id="configImage" <c:if test="${config.hideImageGallery}"> style="display:none" </c:if>
		class="col-xs-12 col-sm-6 col-lg-4">
		<product:productImagePanel galleryImages="${galleryImages}"/>
	</div>
</div>




