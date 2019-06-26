<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="invoiceData" required="true" type="de.hybris.platform.accountsummaryaddon.document.data.B2BDocumentData" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="col-lg-12">
    <div class="col-sm-4 item-wrapper">
      <div class="item-group">
      	<c:if test="${invoiceData.documentNumber ne null}">
        	<ycommerce:testId code="invoiceDetail_invoiceNumber_label">
        		<span class="item-label"><spring:theme code="text.account.invoice.invoiceNumber"/></span>
            	<span class="item-value">${fn:escapeXml(invoiceData.documentNumber)}</span>
           </ycommerce:testId>
        </c:if>
      </div>
      <div class="item-group">
        <c:if test="${invoiceData.invoiceAmount ne null}">
            <ycommerce:testId code="invoiceDetail_invoiceTotal_label">
                <span class="item-label"><spring:theme code="text.account.invoice.invoiceTotal"/></span>
                <span class="item-value"><format:price priceData="${invoiceData.invoiceAmount}"/></span>
            </ycommerce:testId>
        </c:if>
      </div>
      <div class="item-group">
      	<c:if test="${invoiceData.invoiceDate ne null}">
        	<ycommerce:testId code="invoiceDetail_invoiceDate_label">
           		<span class="item-label"><spring:theme code="text.account.invoice.invoiceDate"/></span>
            	<span class="item-value">${fn:escapeXml(invoiceData.invoiceDate)}</span>
       		 </ycommerce:testId>
        </c:if>
      </div>
    </div>
    <div class="col-sm-4 item-wrapper">
      <div class="item-group">
      	<c:if test="${invoiceData.orderNumber ne null}">
        	<ycommerce:testId code="invoiceDetail_orderNumber_label">
            	<span class="item-label"><spring:theme code="text.account.invoice.orderNumber"/></span>
            	<span class="item-value">${fn:escapeXml(invoiceData.orderNumber)}</span>
        	</ycommerce:testId>
        </c:if>
      </div>
      <div class="item-group">
      	<c:if test="${invoiceData.invoiceDate ne null}">
        	<ycommerce:testId code="invoiceDetail_orderPlaced_label">
           		<span class="item-label"><spring:theme code="text.account.invoice.orderPlaced"/></span>
           		<span class="item-value">${fn:escapeXml(invoiceData.invoiceDate)}</span>
        	</ycommerce:testId>
        </c:if>
      </div>
      <div class="item-group">
        <c:if test="${invoiceData.customerNumber ne null}">
            <ycommerce:testId code="invoiceDetail_customerNumbercustomerNumber_label">
                <span class="item-label"><spring:theme code="text.account.invoice.customerNumber"/></span>
            	<span class="item-value">${fn:escapeXml(invoiceData.customerNumber)}</span>
            </ycommerce:testId>
        </c:if>
      </div>
    </div>
    <div class="col-sm-4 item-wrapper">
        <c:if test="${invoiceData.deliveryNumber ne null}">
        	<ycommerce:testId code="invoiceDetail_deliveryNumber_label">
        		<span class="item-label"><spring:theme code="text.account.invoice.deliveryNumber"/></span>
            	<span class="item-value">${fn:escapeXml(invoiceData.deliveryNumber)}</span>
           </ycommerce:testId>
        </c:if>
        <c:if test="${invoiceData.deliveryDate ne null}">
        	<ycommerce:testId code="invoiceDetail_deliveryDate_label">
        		<span class="item-label"><spring:theme code="text.account.order.deliveryDate"/></span>
            	<span class="item-value">${fn:escapeXml(invoiceData.deliveryDate)}</span>
           </ycommerce:testId>
        </c:if>
        <c:if test="${invoiceData.ourTaxNumber ne null}">
        	<ycommerce:testId code="invoiceDetail_ourTaxNumber_label">
        		<span class="item-label"><spring:theme code="text.account.order.ourTaxNumber"/></span>
            	<span class="item-value">${fn:escapeXml(invoiceData.ourTaxNumber)}</span>
           </ycommerce:testId>
        </c:if>
    </div>
</div>

