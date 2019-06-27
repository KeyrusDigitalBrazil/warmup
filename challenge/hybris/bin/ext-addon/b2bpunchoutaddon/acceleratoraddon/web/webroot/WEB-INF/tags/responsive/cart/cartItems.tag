<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="grid" tagdir="/WEB-INF/tags/responsive/grid" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/responsive/storepickup" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

 <spring:htmlEscape defaultHtmlEscape="true" />
 <c:set var="errorStatus" value="<%= de.hybris.platform.catalog.enums.ProductInfoStatus.valueOf(\"ERROR\") %>" />

 <ul class="item__list item__list__cart">
    <li class="hidden-xs hidden-sm">
        <ul class="item__list--header">
            <li class="item__toggle"></li>
            <li class="item__image"></li>
            <li class="item__info"><spring:theme code="basket.page.item"/></li>
            <li class="item__price"><spring:theme code="basket.page.price"/></li>
            <li class="item__quantity"><spring:theme code="basket.page.qty"/></li>
            <li class="item__delivery"><spring:theme code="basket.page.delivery"/></li>
            <li class="item__total--column"><spring:theme code="basket.page.total"/></li>
            <li class="item__remove"></li>
        </ul>
    </li>

    <c:forEach items="${cartData.entries}" var="entry" varStatus="loop">

        <c:if test="${not empty entry.statusSummaryMap}" >
            <c:set var="errorCount" value="${entry.statusSummaryMap.get(errorStatus)}"/>
            <c:if test="${not empty errorCount && errorCount > 0}" >
                <div class="notification has-error">
                    <spring:theme code="basket.error.invalid.configuration" arguments="${errorCount}"/>
                    <spring:theme code="basket.error.invalid.configuration.edit"/>
                </div>
            </c:if>
        </c:if>
        <c:set var="showEditableGridClass" value=""/>
    
        <li class="item__list--item">
            <%-- chevron for multi-d products --%>
            <div class="hidden-xs hidden-sm item__toggle">
                <c:if test="${entry.product.multidimensional}" >
                    <div class="js-show-editable-grid" data-index="${loop.index}" data-read-only-multid-grid="${not entry.updateable}">
                        <ycommerce:testId code="cart_product_updateQuantity">
                            <span class="glyphicon glyphicon-chevron-down"></span>
                        </ycommerce:testId>
                    </div>
                </c:if>
            </div>

            <%-- product image --%>
            <div class="item__image">
                <product:productPrimaryImage product="${entry.product}" format="thumbnail"/>
            </div>

            <%-- product name, code, promotions --%>
            <div class="item__info">
                <ycommerce:testId code="cart_product_name">
                   <span class="item__name">${fn:escapeXml(entry.product.name)}</span>
                </ycommerce:testId>

                <div class="item__code">${fn:escapeXml(entry.product.code)}</div>

                <c:if test="${entry.product.configurable}">
                    <div class="item__configurations">
                        <c:forEach var="config" items="${entry.configurationInfos}">
                        <c:set var="style" value=""/>
                            <c:if test="${config.status eq errorStatus}">
                                <c:set var="style" value="color:red"/>
                            </c:if>
                            <div class="item__configuration--entry " style="${fn:escapeXml(style)}">
                                <div class="row">
                                    <div class="item__configuration--name col-md-4">${fn:escapeXml(config.configurationLabel)}:</div>
                                    <div class="item__configuration--value col-md-8">${fn:escapeXml(config.configurationValue)}</div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <c:if test="${not empty entry.configurationInfos}">
                        <div class="item__configurations--edit">
                            <spring:theme code="basket.page.change.configuration"/>
                        </div>
                    </c:if>
                </c:if>

                <%-- availability --%>
                <div class="item__stock">
                    <c:set var="entryStock" value="${fn:escapeXml(entry.product.stock.stockLevelStatus.code)}"/>
                    <c:forEach items="${entry.product.baseOptions}" var="option">
                        <c:if test="${not empty option.selected and option.selected.url eq entry.product.url}">
                            <c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
                                <div>
                                    <strong>${fn:escapeXml(selectedOption.name)}:</strong>
                                    <span>${fn:escapeXml(selectedOption.value)}</span>
                                </div>
                                <c:set var="entryStock" value="${fn:escapeXml(option.selected.stock.stockLevelStatus.code)}"/>
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

                <c:if test="${ycommerce:doesPotentialPromotionExistForOrderEntry(cartData, entry.entryNumber)}">
                    <c:forEach items="${cartData.potentialProductPromotions}" var="promotion">
                        <c:set var="displayed" value="false"/>
                        <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
                            <c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber && not empty promotion.description}">
                                <c:set var="displayed" value="true"/>

                                    <div class="promo">
                                         <ycommerce:testId code="cart_potentialPromotion_label">
                                             ${ycommerce:sanitizeHTML(promotion.description)}
                                         </ycommerce:testId>
                                    </div>
                            </c:if>
                        </c:forEach>
                    </c:forEach>
                </c:if>
                <c:if test="${ycommerce:doesAppliedPromotionExistForOrderEntry(cartData, entry.entryNumber)}">
                    <c:forEach items="${cartData.appliedProductPromotions}" var="promotion">
                        <c:set var="displayed" value="false"/>
                        <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
                            <c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
                                <c:set var="displayed" value="true"/>
                                <div class="promo">
                                    <ycommerce:testId code="cart_appliedPromotion_label">
                                        ${ycommerce:sanitizeHTML(promotion.description)}
                                    </ycommerce:testId>
                                </div>
                            </c:if>
                        </c:forEach>
                    </c:forEach>
                </c:if>
            </div>

            <%-- price --%>
            <div class="item__price">
                <span class="visible-xs visible-sm"><spring:theme code="basket.page.itemPrice"/>: </span>
                <format:price priceData="${entry.basePrice}" displayFreeForZero="true"/>
            </div>

            <%-- quantity --%>
            <div class="item__quantity hidden-xs hidden-sm">
                <c:choose>
                    <c:when test="${not entry.product.multidimensional}" >
                        <c:url value="/cart/update" var="cartUpdateFormAction" />
                        <c:set var="cartDataJson">
                        	{
                        		"cartCode":"${ycommerce:encodeJSON(cartData.code)}",
                        		"productPostPrice":"${ycommerce:encodeJSON(entry.basePrice.value)}",
                        		"productName":"${ycommerce:encodeJSON(entry.product.name)}"
                        	}
                        </c:set>
                        <form:form id="updateCartForm${loop.index}" action="${cartUpdateFormAction}" method="post" commandName="updateQuantityForm${entry.entryNumber}"
                                    class="js-qty-form${loop.index}" data-cart="${fn:escapeXml(cartDataJson)}">
                            <input type="hidden" name="entryNumber" value="${fn:escapeXml(entry.entryNumber)}"/>
                            <input type="hidden" name="productCode" value="${fn:escapeXml(entry.product.code)}"/>
                            <input type="hidden" name="initialQuantity" value="${fn:escapeXml(entry.quantity)}"/>
                            <ycommerce:testId code="cart_product_quantity">
                                <form:label cssClass="visible-xs visible-sm" path="quantity" for="quantity${entry.entryNumber}"></form:label>
                                <form:input cssClass="form-control js-update-entry-quantity-input" disabled="${not entry.updateable}" type="text" size="1" id="quantity_${loop.index}" path="quantity" />
                            </ycommerce:testId>
                        </form:form>
                    </c:when>
                    <c:otherwise>
                        <c:url value="/cart/updateMultiD" var="cartUpdateMultiDFormAction" />
                        <form:form id="updateCartForm${loop.index}" action="${cartUpdateMultiDFormAction}" method="post" class="js-qty-form${loop.index}" commandName="updateQuantityForm${loop.index}">
                            <input type="hidden" name="entryNumber" value="${fn:escapeXml(entry.entryNumber)}"/>
                            <input type="hidden" name="productCode" value="${fn:escapeXml(entry.product.code)}"/>
                            <input type="hidden" name="initialQuantity" value="${fn:escapeXml(entry.quantity)}"/>
                            <label class="visible-xs visible-sm"><spring:theme code="basket.page.qty"/>:</label>
                            <span class="qtyValue"><c:out value="${entry.quantity}" /></span>
                            <input type="hidden" name="quantity" value="0"/>
                            <ycommerce:testId code="cart_product_updateQuantity">
                                <div id="QuantityProduct${loop.index}" class="updateQuantityProduct"></div>
                            </ycommerce:testId>
                        </form:form>
                    </c:otherwise>
                </c:choose>
            </div>

            <%-- delivery --%>
            <div class="item__delivery">
                <c:if test="${entry.product.purchasable}">
                    <c:if test="${not empty entryStock and entryStock ne 'outOfStock'}">
                        <c:if test="${entry.deliveryPointOfService eq null or not entry.product.availableForPickup}">
                            <span class="item__delivery--label"><spring:theme code="basket.page.shipping.ship"/></span>
                        </c:if>
                    </c:if>
                    <c:if test="${not empty entry.deliveryPointOfService.name}">
                        <span class="item__delivery--label"><spring:theme code="basket.page.shipping.pickup"/></span>
                    </c:if>

                    <c:if test="${entry.product.availableForPickup and not empty entry.deliveryPointOfService.name}">
                        <div class="item__delivery--store">${fn:escapeXml(entry.deliveryPointOfService.name)}</div>
                    </c:if>
                </c:if>
            </div>

            <%-- total --%>
            <ycommerce:testId code="cart_totalProductPrice_label">
                <div class="item__total js-item-total hidden-xs hidden-sm">
                    <format:price priceData="${entry.totalPrice}" displayFreeForZero="true"/>
                </div>
            </ycommerce:testId>

            <%-- remove icon --%>
            <div class="item__remove">
                <c:if test="${entry.updateable}" >
                    <ycommerce:testId code="cart_product_removeProduct">
                        <c:choose>
                            <c:when test="${not entry.product.multidimensional}" >
                                <button class="btn js-remove-entry-button" id="removeEntry_${loop.index}">
                                    <span class="glyphicon glyphicon-remove"></span>
                                </button>
                            </c:when>
                            <c:otherwise>
                                <button class="btn js-submit-remove-product-multi-d" data-index="${loop.index}"  id="removeEntry_${loop.index}">
                                    <span class="glyphicon glyphicon-remove"></span>
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </ycommerce:testId>
                </c:if>
            </div>

            <div class="item__quantity__total visible-xs visible-sm">
                <c:if test="${entry.product.multidimensional}" >
                    <ycommerce:testId code="cart_product_updateQuantity">
                        <c:set var="showEditableGridClass" value="js-show-editable-grid"/>
                    </ycommerce:testId>
                </c:if>
                <div class="details ${showEditableGridClass}" data-index="${loop.index}" data-read-only-multid-grid="${not entry.updateable}">
                    <div class="qty">
                        <c:choose>
                            <c:when test="${not entry.product.multidimensional}" >
                                <c:url value="/cart/update" var="cartUpdateFormAction" />
                                <c:set var="cartDataJson">
                                	{
                                		"cartCode":"${ycommerce:encodeJSON(cartData.code)}",
                                		"productPostPrice":"${ycommerce:encodeJSON(entry.basePrice.value)}",
                                		"productName":"${ycommerce:encodeJSON(entry.product.name)}"
                                	}
                                </c:set>
                                <form:form id="updateCartForm${loop.index}" action="${cartUpdateFormAction}" method="post" commandName="updateQuantityForm${entry.entryNumber}"
                                            class="js-qty-form${loop.index}" data-cart="${fn:escapeXml(cartDataJson)}">
                                    <input type="hidden" name="entryNumber" value="${fn:escapeXml(entry.entryNumber)}"/>
                                    <input type="hidden" name="productCode" value="${fn:escapeXml(entry.product.code)}"/>
                                    <input type="hidden" name="initialQuantity" value="${fn:escapeXml(entry.quantity)}"/>
                                    <ycommerce:testId code="cart_product_quantity">
                                        <form:label cssClass="" path="quantity" for="quantity${entry.entryNumber}"><spring:theme code="basket.page.qty"/>:</form:label>
                                        <form:input cssClass="form-control js-update-entry-quantity-input" disabled="${not entry.updateable}" type="text" size="1" id="quantity_${loop.index}" path="quantity" />
                                    </ycommerce:testId>
                                </form:form>
                            </c:when>
                            <c:otherwise>
                                <c:url value="/cart/updateMultiD" var="cartUpdateMultiDFormAction" />
                                <form:form id="updateCartForm${loop.index}" action="${cartUpdateMultiDFormAction}" method="post" class="js-qty-form${loop.index}" commandName="updateQuantityForm${loop.index}">
                                    <input type="hidden" name="entryNumber" value="${fn:escapeXml(entry.entryNumber)}"/>
                                    <input type="hidden" name="productCode" value="${fn:escapeXml(entry.product.code)}"/>
                                    <input type="hidden" name="initialQuantity" value="${fn:escapeXml(entry.quantity)}"/>
                                    <label><spring:theme code="basket.page.qty"/>:</label>
                                    <span class="qtyValue"><c:out value="${entry.quantity}" /></span>
                                    <input type="hidden" name="quantity" value="0"/>
                                    <ycommerce:testId code="cart_product_updateQuantity">
                                        <div id="QuantityProduct${loop.index}" class="updateQuantityProduct"></div>
                                    </ycommerce:testId>
                                </form:form>
                            </c:otherwise>
                        </c:choose>
                        <c:if test="${entry.product.multidimensional}" >
                            <ycommerce:testId code="cart_product_updateQuantity">
                                <span class="glyphicon glyphicon-chevron-right"></span>
                            </ycommerce:testId>
                        </c:if>
                        <ycommerce:testId code="cart_totalProductPrice_label">
                            <div class="item__total js-item-total">
                                <format:price priceData="${entry.totalPrice}" displayFreeForZero="true"/>
                            </div>
                        </ycommerce:testId>
                    </div>
                </div>
            </div>
        </li>
        
        <li>
        	<spring:url value="/cart/getProductVariantMatrix" var="targetUrl"/>
			<grid:gridWrapper entry="${entry}" index="${loop.index}" styleClass="add-to-cart-order-form-wrap display-none" 
				targetUrl="${targetUrl}"/>
		</li>
    </c:forEach>
</ul>

<product:productOrderFormJQueryTemplates />
<storepickup:pickupStorePopup />
