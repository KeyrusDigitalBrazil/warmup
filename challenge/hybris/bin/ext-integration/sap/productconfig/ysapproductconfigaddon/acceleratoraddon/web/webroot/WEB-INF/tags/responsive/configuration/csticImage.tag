<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ attribute name="cstic" type="de.hybris.platform.sap.productconfig.facades.CsticData"%>
<%@ attribute name="csticKey" required="true" type="java.lang.String"%>

<c:if test="${fn:length(cstic.media) gt 0}">
	<div class="cpq-cstic-images">
		<c:forEach var="medium" items="${cstic.media}" varStatus="status">
			<c:if test="${medium.format eq 'CSTIC_IMAGE'}">
				<div id="${csticKey}.imgContainer" class="cpq-cstic-image-container">
					<img id="${csticKey}.img${status.index}" class="cpq-cstic-image" alt="${cstic.langdepname}" src="${medium.url}"/>
				</div>
			</c:if>		
		</c:forEach>
	</div>
</c:if>
