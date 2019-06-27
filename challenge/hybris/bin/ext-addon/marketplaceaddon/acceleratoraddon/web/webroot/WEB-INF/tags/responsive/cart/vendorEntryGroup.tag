<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="entryGroup" required="true" type="de.hybris.platform.commercefacades.order.EntryGroupData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%--
   Represents root entry group on cart page
--%>

<spring:htmlEscape defaultHtmlEscape="true"/>

<table>
    <c:choose>
        <c:when test="${not empty entryGroup.children}">
            <c:forEach items="${entryGroup.children}" var="group" varStatus="loop">
				<c:if test="${group.groupType.code == 'VENDOR'}">
					 <tr>
						<td>
							<table>
								<tr class="entry-group-header">
									<c:choose>
										<c:when test="${group.erroneous}">
											<th class="error">
												<div class="row">
													<div class="col-md-10 col-lg-11 col-sm-9 left-align">
															${fn:escapeXml(group.label)}
														<p class="entry-group-error-message">
															<spring:theme code="basket.validation.invalidGroup"/>
														</p>
													</div>
												</div>
											</th>
										</c:when>
										<c:otherwise>
											<th>
												<div class="row">
													<div class="col-md-10 col-lg-11 col-sm-9 left-align">
														<c:url value="${group.orderEntries[0].product.vendor.url}" var="vendorUrl"/>
														<a href="${fn:escapeXml(vendorUrl)}">${fn:escapeXml(group.label)}</a>
													</div>
												</div>
											</th>
										</c:otherwise>
									</c:choose>
								</tr>
								<c:if test="${not empty group.orderEntries}">
									<c:forEach items="${group.orderEntries}" var="entry">
										<tr>
											<td>
												<cart:cartItem cartData="${cartData}" entry="${entry}" index="${group.groupNumber}"/>
											</td>
										</tr>
									</c:forEach>
								</c:if>
							</table>
						</td>
					</tr>
				</c:if>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <c:if test="${not empty entryGroup.orderEntries}">
                <c:forEach items="${entryGroup.orderEntries}" var="entry">
                    <tr>
                        <td>
                            <cart:cartItem cartData="${cartData}" entry="${entry}" index="${entryGroup.groupNumber}"/>
                        </td>
                    </tr>
                </c:forEach>
            </c:if>
        </c:otherwise>
    </c:choose>
</table>