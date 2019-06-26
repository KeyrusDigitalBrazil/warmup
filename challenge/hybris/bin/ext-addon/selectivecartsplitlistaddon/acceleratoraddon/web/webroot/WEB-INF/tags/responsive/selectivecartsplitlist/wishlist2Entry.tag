<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="wishlist2Data" required="true" type="de.hybris.platform.selectivecartfacades.data.Wishlist2Data"%>
<%@ attribute name="entries" required="true" type="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="selectivecart" tagdir="/WEB-INF/tags/addons/selectivecartsplitlistaddon/responsive/selectivecartsplitlist"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:if test="${not empty entries}">
	<c:forEach items="${entries}" var="entry">
		<table>
			<tr>
				<td><selectivecart:wishlist2Item wishlist2Data="${wishlist2Data}" entry="${entry}" /></td>
			</tr>
		</table>
		<p></p>
	</c:forEach>
</c:if>