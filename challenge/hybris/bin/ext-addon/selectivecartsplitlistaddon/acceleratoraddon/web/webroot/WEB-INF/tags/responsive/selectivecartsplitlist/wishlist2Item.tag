<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="wishlist2Data" required="true" type="de.hybris.platform.selectivecartfacades.data.Wishlist2Data" %>
<%@ attribute name="entry" required="true" type="de.hybris.platform.selectivecartfacades.data.Wishlist2EntryData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="grid" tagdir="/WEB-INF/tags/responsive/grid" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:if test="${not empty entry}">


        <c:set var="showEditableGridClass" value=""/>
        <spring:url value="${entry.product.url}" var="productUrl" htmlEscape="false"/>

        <li class="item__list--item wishlist-item">
        
            <%-- chevron for multi-d products --%>
            <div class="hidden-xs hidden-sm item__toggle">
                <c:if test="${entry.product.multidimensional}" >
                    <div class="js-show-editable-grid" data-index="${fn:escapeXml(index)}" data-read-only-multid-grid="${not entry.updateable}">
                        <ycommerce:testId code="cart_product_updateQuantity">
                            <span class="glyphicon glyphicon-chevron-down"></span>
                        </ycommerce:testId>
                    </div>
                </c:if>
            </div>

            <%-- product image --%>
            <div class="item__image">
                <a href="${fn:escapeXml(productUrl)}"><product:productPrimaryImage product="${entry.product}" format="thumbnail"/></a>
            </div>

            <%-- product name, code, promotions --%>
            <div class="item__info">
                <ycommerce:testId code="cart_product_name">
                    <a href="${fn:escapeXml(productUrl)}"><span class="item__name">${fn:escapeXml(entry.product.name)}</span></a>
                </ycommerce:testId>

                <div class="item__code">${fn:escapeXml(entry.product.code)}</div>

                <%-- availability --%>
                <div class="item__stock">
                    <c:set var="entryStock" value="${entry.product.stock.stockLevelStatus.code}"/>
                    <c:forEach items="${entry.product.baseOptions}" var="option">
                        <c:if test="${not empty option.selected and option.selected.url eq entry.product.url}">
                            <c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
                                <div>
                                    <strong>${fn:escapeXml(selectedOption.name)}:</strong>
                                    <span>${fn:escapeXml(selectedOption.value)}</span>
                                </div>
                                <c:set var="entryStock" value="${option.selected.stock.stockLevelStatus.code}"/>
                            </c:forEach>
                        </c:if>
                    </c:forEach>

                    <div>
                        <c:choose>
                            <c:when test="${not empty entryStock and entryStock ne 'outOfStock' or entry.product.multidimensional}">
                                <span class="stock"><spring:theme code="product.variants.in.stock"/></span>
                            </c:when>
                            <c:otherwise>
                                <span class="out-of-stock"><spring:theme code="product.variants.out.of.stock"/></span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>


            </div>

            <%-- price --%>
            <div class="item__price">
                <span class="visible-xs visible-sm"><spring:theme code="basket.page.itemPrice"/>: </span>
                <format:price priceData="${entry.product.price}" displayFreeForZero="true"/>
            </div>

            <%-- quantity --%>
            <div class="item__quantity hidden-xs hidden-sm">
                <c:choose>
                    <c:when test="${not entry.product.multidimensional}" >
                        <input readonly="readonly" name="quantity" class="form-control js-update-entry-quantity-input" value="${fn:escapeXml(entry.quantity)}"/>
                    </c:when>
                    <c:otherwise>
                    </c:otherwise>
                </c:choose>
            </div>
            

            <%-- action --%>
            <div class="item__action">
                <form:form  action="" method="POST">
                     <spring:url value="/cart/entries/check" var="checkUrl" htmlEscape="false"/>
                     <span class="js-wishlist-select-item saveforlater__action" 
                         data-checkurl="${fn:escapeXml(checkUrl)}" data-productcode="${fn:escapeXml(entry.product.code)}">               
                         <a href="#"><spring:theme code="saveforlater.entry.action.movetocart"/></a>
                     </span>
                
                     <spring:url value="/cart/entries/remove" var="removeActionUrl" htmlEscape="false"/>
                     <span class="js-wishlist2-entry-action-remove-button saveforlater__action" 
                                 data-removeurl="${fn:escapeXml(removeActionUrl)}" data-productcode="${fn:escapeXml(entry.product.code)}">
                          <a href="#" ><spring:theme code="saveforlater.entry.action.remove"/></a>
                     </span>
                 </form:form> 
             </div>

        </li>

 </c:if>
